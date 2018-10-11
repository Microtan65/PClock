package stevebullimore.pclock.msg

import org.joda.time.DateTime
import stevebullimore.pclock.display.Font5x7
import stevebullimore.pclock.animsup.messages._
import akka.actor.Actor
import org.joda.time.Duration
import stevebullimore.pclock.display.Digit3x5

class TimerAnimation extends Actor {

  override def receive = animate(new DateTime(), "active", "finished")
  
  private def animate(endTime: DateTime, activeMessage: String, finishedMessage: String): Receive = {
     case AnimationInit(time, data) =>
      sender() ! Frame(List())
      val params = data.getOrElse("Timer will expire in,Timer finished,600").split(",")
      context.become(animate(time.plusSeconds(params(2).toInt), params(0), params(1)))
      
    case Animate(time) =>
      val duration = new Duration(time, endTime)
      val millis = duration.getMillis()
      sender() ! Frame(
        drawNumber(8, 1, 10, math.max((millis / 3600000).toInt, 0), List()) ++
        drawNumber(18, 1, 10, math.max(((millis % 3600000) / 60000).toInt, 0), List()) ++
        drawNumber(28, 1, 10, math.max(((millis % 60000) / 1000).toInt, 0), List()) ++
        drawNumber(36, 1, 1, math.max(((millis % 1000) / 100).toInt, 0), List()) ++
        List((35, 5)) ++ (if (time.getMillisOfSecond < 500) List((16, 2), (16, 4), (26, 2), (26, 4)) else List()))    
        
    case AskStatus(time) =>
      val duration = new Duration(time, endTime)
      val millis = duration.getMillis()
      sender() ! AnimationStatus(if (millis > 0) activeMessage + timeLeftText(millis) else finishedMessage) 
  }
  
  private def drawNumber(x: Int, y: Int, exp: Int, n: Int, pixels: List[(Int, Int)]): List[(Int, Int)] = {
    if (exp == 0) pixels
    else drawNumber(x + 4, y, exp / 10, n, Digit3x5.draw(x, y, (n / exp) % 10) ++ pixels)
  }
  
  private def timeLeftText(millis: Long): String = {
    " in %d hours, %d minutes, %d seconds".format(millis / 3600000, (millis % 3600000) / 60000, (millis % 60000) / 1000)
  }
}
