package stevebullimore.pclock.rss

import org.joda.time.DateTime
import scala.concurrent.{Future, ExecutionContext}
import scala.xml.XML
import scala.util.{Try, Success, Failure}
import akka.actor.{Actor, ActorRef, Props, Status}
import akka.pattern.pipe
import spray.http._
import spray.client.pipelining._
import stevebullimore.pclock.animsup.messages._
import stevebullimore.pclock.msg.MsgAnimation

class RssAnimation extends Actor {
  import context.dispatcher

  val msgAnimation = context.system.actorOf(Props[MsgAnimation])
  
  override def receive = idle()
 
  def idle(): Receive = {
    case AnimationInit(_, data) =>
      val url = data.getOrElse("")
      initiateRead(url)
      context.become(readingFeed(url, sender()))
  }
  
  def readingFeed(url: String, anim: ActorRef): Receive = {
    case resp: HttpResponse =>
      Try {
        XML.loadString(resp.entity.asString(HttpCharsets.`UTF-8`)) \ "channel" \ "item" \ "description"
      } match {
        case Success(itemNodes) =>
          val items = for (item <- itemNodes) yield item.text
          msgAnimation ! AnimationInit(new DateTime(), Option(items(0)))
        case Failure(error) =>
          msgAnimation ! AnimationInit(new DateTime(), Option("Error parsing feed XML"))
      }
      context.become(showingFeed(anim))
    case Status.Failure(error) =>
      msgAnimation ! AnimationInit(new DateTime(), Option(s"Error contacting $url"))
      context.become(showingFeed(anim))      
  }
  
  def showingFeed(anim: ActorRef): Receive = {
    case animate: Animate =>
      msgAnimation ! animate
    case frame: Frame =>
      anim ! frame
    case ended: AnimationEnded =>
      anim ! ended
      context.become(idle())
  }
      
  def initiateRead(url: String) = {
    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
    pipeline(Get(url)).pipeTo(self)
  }
}
