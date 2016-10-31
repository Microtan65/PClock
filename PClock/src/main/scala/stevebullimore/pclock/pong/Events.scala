package stevebullimore.pclock.pong



case class BallPassNetTowardRight(predictedY: Double, winning: Boolean) extends AnimationEvent
case class BallPassNetTowardLeft(predictedY: Double, winning: Boolean) extends AnimationEvent
case class NewGame() extends AnimationEvent
