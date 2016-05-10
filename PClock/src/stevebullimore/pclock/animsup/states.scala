package stevebullimore.pclock.animsup

object states {
  case class RunningContinuous(id: Int)
  case class RunningFinite(id: Int, data: Option[String], freq: Option[Int], contId: Int)
}