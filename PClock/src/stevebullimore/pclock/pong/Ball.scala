package stevebullimore.pclock.pong

import org.joda.time.DateTime

case class Ball(xPos: Double, yPos: Double, xSpeed: Double, ySpeed: Double, xBounce: Boolean) extends AnimationEntity {

  private def predictY(): Double = {
    val y = math.round(yPos + 23 * ySpeed)
    if (y < 0) 0 - y else if (y > 15) 15 - (y - 15) else y
  }

  override def getEvents(time: DateTime): List[AnimationEvent] = {
    if (xSpeed < 0 && xPos > 23 && (xPos + xSpeed <= 23))
      List(BallPassNetTowardLeft(predictY(), time.getSecondOfMinute > 56 && time.getMinuteOfHour != 59))
    else if (xPos < 24 && (xPos + xSpeed >= 24))
      List(BallPassNetTowardRight(predictY(), time.getSecondOfMinute > 56 && time.getMinuteOfHour == 59))
    else
      List()

  }

  override def update(events: List[AnimationEvent], time: DateTime): AnimationEntity = {
    if (events.contains(NewGame())) Ball(23, 7, 1, 0.8, true)
    else {
      val newXBounce = events.collectFirst{ 
        case BallPassNetTowardLeft(_, win) => !win
        case BallPassNetTowardRight(_, win) => !win
      } getOrElse(xBounce)

      val minX = if (newXBounce) 0 else -1
      val maxX = if (newXBounce) 47 else 48
      
      val speedFactor = 0.5 + (time.getSecondOfMinute + 1).toDouble / 120.0

      val newX = math.max(minX, math.min(xPos + xSpeed * speedFactor, maxX))
      val newY = math.max(0, math.min(yPos + ySpeed * speedFactor, 15))
      

      val newYSpeed = if (newY == 15 || newY == 0) 0 - ySpeed else ySpeed
      val newXSpeed = if (newXBounce && (newX >= 47 || newX <= 0)) 0 - xSpeed else xSpeed

      Ball(newX, newY, newXSpeed, newYSpeed, newXBounce)
    }
  }

  override def draw(): List[(Int, Int)] = List((xPos.toInt, yPos.toInt))
}
