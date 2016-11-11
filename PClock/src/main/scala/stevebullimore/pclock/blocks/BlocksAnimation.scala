package stevebullimore.pclock.blocks

import akka.actor.Actor
import stevebullimore.pclock.animsup.messages._

class BlocksAnimation extends Actor {

  private val blockPos = (22,1) :: (26, 3) :: (30, 5) :: (34, 7) :: (30, 9) :: (26, 11) :: (22, 13) :: (18, 11) :: (14, 9) :: (10, 7) :: (14, 5) :: (18, 3) :: Nil
  
  override def receive = {
    case AnimationInit(_, _) => 
      sender() ! Frame(List())
    
    case Animate(time) =>
      sender() ! Frame(drawHour(time.getHourOfDay % 12, time.getMinuteOfHour + (1 - (time.getMillisOfSecond / 500)) * 6, List()))
  }
  
  private def drawHour(hour: Int, minute: Int, pixels: List[(Int, Int)]): List[(Int, Int)] = {
    def drawMinutes(x: Int, y: Int, minute: Int, pixels: List[(Int, Int)]): List[(Int, Int)] = {
      if (minute < 6) pixels
      else drawMinutes(x, y, minute - 6, (x + (((minute / 6) - 1)  % 3), y + ((minute - 6) / 18)) :: pixels)
    }
    
    if (hour == -1 ) pixels
    else drawHour(hour - 1, 59, drawMinutes(blockPos(hour)._1, blockPos(hour)._2, minute, pixels))
  }
}
