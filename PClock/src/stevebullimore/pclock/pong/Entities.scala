package stevebullimore.pclock.pong

import org.joda.time.DateTime
import scala.annotation.tailrec
import stevebullimore.pclock.{AnimationEntity, AnimationEvent, Digit3x5}

case class Ball(xPos: Double, yPos: Double, xSpeed: Double, ySpeed: Double) extends AnimationEntity {

  private def predictY(): Double = {
    val y = yPos + 23 * ySpeed
    if (y < 0) 0 - y else if (y > 15) 15 - (y - 15) else y
  }
  
  override def getEvents(time: DateTime): List[AnimationEvent] = {
    if (xSpeed < 0 && xPos > 23 && (xPos + xSpeed <= 23))
      List(BallPassNetTowardLeft(predictY()))
    else if (xPos < 23 && (xPos + xSpeed >= 23))
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

trait PaddleEntity extends AnimationEntity {
  override def getEvents(time: DateTime): List[AnimationEvent] = List()
  override def draw(): List[(Int, Int)]  = List((xPos.toInt, yPos.toInt), (xPos.toInt, (yPos+1).toInt), (xPos.toInt, (yPos+2).toInt), (xPos.toInt, (yPos+3).toInt)) 
  
}

case class LeftPaddle(yPos: Double, yDest: Double) extends PaddleEntity {
  override val xPos: Double = 0
  override def update(events: List[AnimationEvent], time: DateTime): AnimationEntity = {
    val newYDest = events.foldLeft(yDest)((y, event) => event match {
      case BallPassNetTowardLeft(predictedY) => predictedY
      case _ => yDest
      } )
    val newYPos = if (yPos > newYDest) yPos - 0.5
    else if (yPos < newYDest) yPos + 0.5
    else yPos
    
    LeftPaddle(newYPos, newYDest)
  }
}

case class RightPaddle(yPos: Double, yDest: Double) extends PaddleEntity {
  override val xPos: Double = 47
  override def update(events: List[AnimationEvent], time: DateTime): AnimationEntity = {
    val newYDest = events.foldLeft(yDest)((y, event) => event match {
      case BallPassNetTowardRight(predictedY) => predictedY // TODO Miss when win
      case _ => yDest
      } )
    val newYPos = if (yPos > newYDest) yPos - 0.5
    else if (yPos < newYDest) yPos + 0.5
    else yPos
    
    RightPaddle(newYPos, newYDest)
  }
}

case class Net(xPos: Double, yPos: Double) extends AnimationEntity {
  override def getEvents(time: DateTime): List[AnimationEvent] = List()
  override def update(events: List[AnimationEvent], time: DateTime): AnimationEntity = this
  override def draw(): List[(Int, Int)]  = {
     @tailrec
     def line(pixels: List[(Int, Int)], y: Int): List[(Int, Int)] = {
       if (y>15) pixels
       else line((xPos.toInt, y) :: pixels, y + 2)
     }
     line(List(), yPos.toInt)
   }
}

case class Score(xPos: Double, yPos: Double, time: DateTime) extends AnimationEntity {
  override def getEvents(latestTime: DateTime): List[AnimationEvent] = { 
    if (latestTime.getSecondOfMinute == 4) List(NewGame())
    else if (latestTime.getSecondOfMinute < 4) {
      if (latestTime.getMinuteOfHour == 0) List(LeftToWin())
      else List(RightToWin())
    }
    else List()
  }
  override def update(events: List[AnimationEvent], time: DateTime): AnimationEntity = {
    if (events.contains(NewGame())) Score(xPos, yPos, time)
    else this
  }
  override def draw(): List[(Int, Int)]  = {
    val x = xPos.toInt
    val y = yPos.toInt
    Digit3x5.draw(x, y, time.getHourOfDay / 10) ++
    Digit3x5.draw(x+4, y, time.getHourOfDay % 10) ++
    Digit3x5.draw(x+10, y, time.getMinuteOfHour / 10) ++
    Digit3x5.draw(x+14, y, time.getMinuteOfHour % 10)
  }
}