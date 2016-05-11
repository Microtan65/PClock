package stevebullimore.pclock.msg

import org.joda.time.DateTime
import stevebullimore.pclock.display.Font5x7
import stevebullimore.pclock.animsup.messages._
import akka.actor.Actor

class MsgAnimation extends Actor {

  override def receive = idle()
  
  private def idle(): Receive = {
    case AnimationInit(time, data) =>
      sender() ! Frame(List())
      context.become(scrollMessage(data.getOrElse("?"), 48, 0))
  }
  
  private def scrollMessage(message: String, x: Int, count: Int): Receive = {
    case Animate(_) =>
      val newCount = if (count == 1) 0 else count + 1
      val newX = if (newCount == 0) x - 1 else x
      if (newX < (message.length() * -6)) {
        sender() ! AnimationEnded()
        context.become(idle())
      } else {
        sender() ! Frame(Font5x7.draw(newX, 4, message))
        context.become(scrollMessage(message, newX, newCount))
      }
  }
}
