package stevebullimore.pclock.animsup

import org.joda.time.DateTime

object messages {
  case class AnimationInit(time: DateTime, data: Option[String])
  case class Animate(time: DateTime)
  case class Frame(pixels: List[(Int, Int)])
  case class AnimationEnded()
  case class SelectContinuousAnim(id: Int, data: Option[String])
  case class SelectFiniteAnim(id: Int, data: Option[String], freq: Option[Int])
  case class AskAnimations()
  case class AskStatus(time: DateTime)
  case class AnimationInfos(infos: List[AnimationInfos.AnimationInfo])
  case class AnimationStatus(status: String)
  object AnimationInfos {
    case class AnimationInfo(id: Int, name: String, continuous: Boolean, active: Boolean)
  }
}