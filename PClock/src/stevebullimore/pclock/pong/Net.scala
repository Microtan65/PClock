package stevebullimore.pclock.pong

import scala.annotation.tailrec
import org.joda.time.DateTime
import stevebullimore.pclock.{AnimationEntity, AnimationEvent}

case class Net(xPos: Double, yPos: Double) extends AnimationEntity {
  override def getEvents(time: DateTime): List[AnimationEvent] = List()
  override def update(events: List[AnimationEvent], time: DateTime): AnimationEntity = this
  override def draw(): List[(Int, Int)] = {
    @tailrec
    def line(pixels: List[(Int, Int)], y: Int): List[(Int, Int)] = {
      if (y > 15) pixels
      else line((xPos.toInt, y) :: pixels, y + 2)
    }
    line(List(), yPos.toInt)
  }
}
