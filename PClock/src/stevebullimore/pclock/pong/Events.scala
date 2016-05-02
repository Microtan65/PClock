package stevebullimore.pclock.pong

import stevebullimore.pclock.AnimationEvent

case class BallPassNetTowardRight(predictedY: Double) extends AnimationEvent
case class BallPassNetTowardLeft(predictedY: Double) extends AnimationEvent
case class RightToWin() extends AnimationEvent
case class LeftToWin() extends AnimationEvent
case class NewGame() extends AnimationEvent
