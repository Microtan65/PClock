package stevebullimore.pclock.http

import scala.concurrent._
import scala.concurrent.duration._

import akka.actor.Actor
import akka.actor.ActorSelection.toScala
import akka.pattern.ask
import akka.util.Timeout
import spray.http.StatusCodes
import spray.httpx.SprayJsonSupport._
import spray.httpx.marshalling.ToResponseMarshallable.isMarshallable
import spray.json.DefaultJsonProtocol
import spray.routing.Directive.pimpApply
import spray.routing.HttpService
import stevebullimore.pclock.animsup.messages._
import stevebullimore.pclock.display.Brightness

class HttpActor extends Actor with ClockHttpService {

  override def actorRefFactory = context

  def receive = {
    runRoute(clockRoute)
  }
}

case class FiniteAnimation(data: Option[String], freq: Option[Int])
case class AnimationData(id: Int, name: String, continuous: Boolean, active: Boolean)

object JsonProtocol extends DefaultJsonProtocol {
  implicit val FiniteAnimationFormat = jsonFormat2(FiniteAnimation)
  implicit val AnimationDataFormat = jsonFormat4(AnimationData)
}

trait ClockHttpService extends HttpService {
  import JsonProtocol._

  // we use the enclosing ActorContext's or ActorSystem's dispatcher for our Futures and Scheduler
  implicit val executionContext = actorRefFactory.dispatcher

  implicit val timeout = Timeout(5 seconds)

  val clockRoute = {
    get {
      path("animation") {
        onSuccess(actorRefFactory.actorSelection("../../AnimationActor").resolveOne) { actor =>
          onSuccess(actor ? AskAnimations()) { 
            case animInfos: AnimationInfos =>
            complete {
              animInfos.infos.map(info => AnimationData(info.id, info.name, info.continuous, info.active))
            }
          }
        }
      }
    } ~
    post {
      path("brightness" / IntNumber) { levelPercent =>
        actorRefFactory.actorSelection("../../AnimationActor") ! Brightness(levelPercent)
        complete(StatusCodes.NoContent)
      } ~
      path("animationc" / IntNumber) { animationIdx =>
        actorRefFactory.actorSelection("../../AnimationActor") ! SelectContinuousAnim(animationIdx)
        complete(StatusCodes.NoContent)
      } ~
      path("animationf" / IntNumber) { animationIdx =>
        entity(as[FiniteAnimation]) { fa => 
          actorRefFactory.actorSelection("../../AnimationActor") ! SelectFiniteAnim(animationIdx, fa.data, fa.freq)
          complete(StatusCodes.NoContent)
        }
      }
    }
  }
}