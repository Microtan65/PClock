package stevebullimore.pclock.blocks

import akka.actor.Actor
import stevebullimore.pclock.animsup.messages._

class BlocksAnimation extends Actor {

  private val blockPos = (27, 3) :: (32, 5) :: (37, 7) :: (32, 9) :: (27, 11) :: (22, 13) :: (17, 11) :: (12, 9) :: (7, 7) :: (12, 5) :: (17, 3) :: (22,1) :: Nil
  
  override def receive = {
    case AnimationInit(_, _) => 
      sender() ! Frame(List())
    
    case Animate(time) =>
      sender() ! Frame(drawHour(time.getHourOfDay % 12, time.getMinuteOfHour + (time.getMillisOfSecond / 500) * 5, List()))
  }
  
  private def drawHour(hour: Int, minute: Int, pixels: List[(Int, Int)]): List[(Int, Int)] = {
    def drawMinutes(x: Int, y: Int, minute: Int, pixels: List[(Int, Int)]): List[(Int, Int)] = {
      if (minute < 5) pixels
      else drawMinutes(x, y, minute - 5, (x + (((minute / 5) - 1)  % 4), y + ((minute - 5) / 20)) :: pixels)
    }
    
    if (hour == -1 ) pixels
    else drawHour(hour - 1, 60, drawMinutes(blockPos(hour)._1, blockPos(hour)._2, minute, pixels))
  }
}
