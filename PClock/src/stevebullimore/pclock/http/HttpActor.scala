package stevebullimore.pclock.http

import scala.concurrent._
import akka.actor.Actor
import akka.util.Timeout
import spray.routing.HttpService
import spray.json.DefaultJsonProtocol
import spray.httpx.SprayJsonSupport._
import spray.http._
import scala.concurrent.duration._
import stevebullimore.pclock.animsup.messages._
import akka.actor.ActorSelection.toScala
import spray.httpx.marshalling.ToResponseMarshallable.isMarshallable
import spray.routing.Directive.pimpApply

class HttpActor extends Actor with ClockHttpService {

  override def actorRefFactory = context

  def receive = {
    runRoute(clockRoute)
  }
}

case class FiniteAnimationStart(data: Option[String], freq: Option[Int])

object JsonProtocol extends DefaultJsonProtocol {
  implicit val FiniteAnimationStartFormat = jsonFormat2(FiniteAnimationStart)
}

trait ClockHttpService extends HttpService {
  import JsonProtocol._

  // we use the enclosing ActorContext's or ActorSystem's dispatcher for our Futures and Scheduler
  implicit def executionContext = actorRefFactory.dispatcher

  implicit val timeout = Timeout(5 seconds)

  val clockRoute = {
   /* get {
      path("animation") {
        onSuccess(actorRefFactory.actorSelection("../../AnimationActor").resolveOne) { actor =>
          //onSuccess(actor ? AskCurrentAnimationMessage()) { result =>
            complete {
              //PClock.animations.foldLeft(List[AnimationInfo]())((infos, animation) =>
              //  AnimationInfo(infos.length, animation.name, infos.length == result) :: infos)
              
              AnimationInfo(0, "Pong", true)
            }
          //}
        }
      }
    } ~*/
    post {
      path("animationc" / IntNumber) { animationIdx =>
        actorRefFactory.actorSelection("../../AnimationActor") ! SelectContinuousAnim(animationIdx)
        complete(StatusCodes.NoContent)
      } ~
      path("animationf" / IntNumber) { animationIdx =>
        entity(as[FiniteAnimationStart]) { fa => 
          actorRefFactory.actorSelection("../../AnimationActor") ! SelectFiniteAnim(animationIdx, fa.data, fa.freq)
          complete(StatusCodes.NoContent)
        }
      }
    }
  }
}