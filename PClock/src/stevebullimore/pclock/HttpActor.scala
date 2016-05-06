package stevebullimore.pclock

import scala.util.Success
import scala.concurrent._
import akka.actor.Actor
import akka.io.Tcp
import akka.util.Timeout
import akka.pattern.ask
import spray.routing.HttpService
import spray.json.DefaultJsonProtocol
import spray.httpx.SprayJsonSupport._
import spray.http._
import spray.can.Http
import scala.concurrent.duration._

class HttpActor extends Actor with ClockHttpService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  override def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing,
  // timeout handling or alternative handler registration
  def receive = {
    runRoute(clockRoute)
  }
}

case class AnimationInfo(id: Int, name: String, active: Boolean)

object JsonProtocol extends DefaultJsonProtocol {
  implicit val AnimationInfoFormat = jsonFormat3(AnimationInfo)
}

trait ClockHttpService extends HttpService {
  import JsonProtocol._

  // we use the enclosing ActorContext's or ActorSystem's dispatcher for our Futures and Scheduler
  implicit def executionContext = actorRefFactory.dispatcher

  implicit val timeout = Timeout(5 seconds)

  val clockRoute = {
    get {
      path("animation") {
        onSuccess(actorRefFactory.actorSelection("../../AnimationActor").resolveOne) { actor =>
          onSuccess(actor ? AskCurrentAnimationMessage()) { result =>
            complete {
              PClock.animations.foldLeft(List[AnimationInfo]())((infos, animation) =>
                AnimationInfo(infos.length, animation.name, infos.length == result) :: infos)
            }
          }
        }
      }
    } ~
    post {
      pathPrefix("animation" / IntNumber) { animationIdx =>
        actorRefFactory.actorSelection("../../AnimationActor") ! ActivateAnimationMessage(animationIdx)
        complete(StatusCodes.NoContent)
      }
    }
  }
}