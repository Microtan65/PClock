package stevebullimore.pclock.pong

import org.joda.time.DateTime

trait PaddleEntity extends AnimationEntity {
  def yDest: Double
  override def getEvents(time: DateTime): List[AnimationEvent] = List()
  override def draw(): List[(Int, Int)] = List((xPos.toInt, yPos.toInt), (xPos.toInt, (yPos + 1).toInt), (xPos.toInt, (yPos + 2).toInt), (xPos.toInt, (yPos + 3).toInt))
  def move(events: List[AnimationEvent], f: AnimationEvent => Double): (Double, Double) = {
    val newYDest = events.foldLeft(yDest)((y, event) => f(event))
    val newYPos = if ((yPos - newYDest) > 0.5) yPos - 0.5
    else if ((yPos - newYDest) < 0.5) yPos + 0.5
    else yPos
    (math.max(0, math.min(11, newYPos)), newYDest)
  }
  def miss(y: Double): Double = {
    val v = y + 5
    if (v > 15) v - 15 else v
  }
}

case class LeftPaddle(yPos: Double, yDest: Double) extends PaddleEntity {
  override val xPos: Double = 0
  override def update(events: List[AnimationEvent], time: DateTime): AnimationEntity = {
    val (newYPos, newYDest) = move(events, event => {event match {
      case BallPassNetTowardLeft(predictedY, win) => if (win) miss(predictedY - 2) else predictedY - 2
      case _                                 => yDest
    } })
    LeftPaddle(newYPos, newYDest)
  }
}

case class RightPaddle(yPos: Double, yDest: Double) extends PaddleEntity {
  override val xPos: Double = 47
  override def update(events: List[AnimationEvent], time: DateTime): AnimationEntity = {
        val (newYPos, newYDest) = move(events, event => {event match {
      case BallPassNetTowardRight(predictedY, win) => if (win) miss(predictedY - 2) else predictedY - 2
      case _                                  => yDest
    } })
    RightPaddle(newYPos, newYDest)
  }
}
