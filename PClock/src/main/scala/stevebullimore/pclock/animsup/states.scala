package stevebullimore.pclock.animsup

import akka.actor.Cancellable

object states {
  case class RunningContinuous(id: Int, cancelFinite: Option[Cancellable])
  case class RunningFinite(id: Int, data: Option[String], freq: Option[Int], contId: Int)
}