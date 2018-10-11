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
import org.joda.time.DateTime

class HttpActor extends Actor with ClockHttpService {

  override def actorRefFactory = context

  def receive = {
    runRoute(clockRoute)
  }
}

case class ContinuousAnimation(data: Option[String])
case class FiniteAnimation(data: Option[String], freq: Option[Int])
case class AnimationData(id: Int, name: String, continuous: Boolean, active: Boolean)

object JsonProtocol extends DefaultJsonProtocol {
  implicit val ContinuousAnimationFormat = jsonFormat1(ContinuousAnimation)
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
      } ~
      path("animationc" / """.*""".r) { animationName =>
        onSuccess(actorRefFactory.actorSelection("../../" + animationName).resolveOne) { actor =>
          onSuccess(actor ? AskStatus(new DateTime())) { 
            case animStatus: AnimationStatus =>
            complete {
              animStatus.status
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
        entity(as[ContinuousAnimation]) { ca => 
          actorRefFactory.actorSelection("../../AnimationActor") ! SelectContinuousAnim(animationIdx, ca.data)
          complete(StatusCodes.NoContent)
        }
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