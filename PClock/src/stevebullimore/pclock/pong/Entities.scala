package stevebullimore.pclock.pong

import org.joda.time.DateTime
import scala.annotation.tailrec
import stevebullimore.pclock.{AnimationEntity, Digit3x5}

case class Ball(xPos: Double, yPos: Double, xSpeed: Double, ySpeed: Double) extends AnimationEntity {
  override def draw(): List[(Int, Int)] = List((xPos.toInt, yPos.toInt))
}

case class Paddle(xPos: Double, yPos: Double) extends AnimationEntity {
   override def draw(): List[(Int, Int)]  = List((xPos.toInt, yPos.toInt), (xPos.toInt, (yPos+1).toInt), (xPos.toInt, (yPos+2).toInt), (xPos.toInt, (yPos+3).toInt)) 
}

case class Net(xPos: Double, yPos: Double) extends AnimationEntity {
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
  override def draw(): List[(Int, Int)]  = {
    val x = xPos.toInt
    val y = yPos.toInt
    Digit3x5.draw(x, y, time.getHourOfDay / 10) ++
    Digit3x5.draw(x+4, y, time.getHourOfDay % 10) ++
    Digit3x5.draw(x+10, y, time.getMinuteOfHour / 10) ++
    Digit3x5.draw(x+14, y, time.getMinuteOfHour % 10)
  }
}