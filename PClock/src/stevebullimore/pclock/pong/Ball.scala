package stevebullimore.pclock.pong

import org.joda.time.DateTime

import stevebullimore.pclock.{AnimationEntity, AnimationEvent}

case class Ball(xPos: Double, yPos: Double, xSpeed: Double, ySpeed: Double) extends AnimationEntity {

  private def predictY(): Double = {
    val y = math.round(yPos + 23 * ySpeed)
    if (y < 0) 0 - y else if (y > 15) 15 - (y - 15) else y
  }

  override def getEvents(time: DateTime): List[AnimationEvent] = {
    if (xSpeed < 0 && xPos > 23 && (xPos + xSpeed <= 23))
      List(BallPassNetTowardLeft(predictY()))
    else if (xPos < 24 && (xPos + xSpeed >= 24))
      List(BallPassNetTowardRight(predictY()))
    else
      List()
  }

  override def update(events: List[AnimationEvent], time: DateTime): AnimationEntity = {
    if (events.contains(NewGame())) Ball(23, 7, 1, 0.8)
    else {
      val leftToWin = events.contains(LeftToWin())
      val rightToWin = events.contains(RightToWin())

      val minX = if (rightToWin) -1 else 0
      val maxX = if (leftToWin) 48 else 47

      val newX = math.max(minX, math.min(xPos + xSpeed, maxX))
      val newY = math.max(0, math.min(yPos + ySpeed, 15))

      val newYSpeed = if (newY == 15 || newY == 0) 0 - ySpeed else ySpeed
      val bounceX = !((xSpeed < 0 && rightToWin) || (xSpeed > 0 && leftToWin))
      val newXSpeed = if (bounceX && (newX == 47 || newX == 0)) 0 - xSpeed else xSpeed

      Ball(newX, newY, newXSpeed, newYSpeed)
    }
  }

  override def draw(): List[(Int, Int)] = List((xPos.toInt, yPos.toInt))
}
