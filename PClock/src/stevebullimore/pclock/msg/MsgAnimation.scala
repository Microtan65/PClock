package stevebullimore.pclock.msg

import org.joda.time.DateTime
import stevebullimore.pclock.display.Font5x7
import stevebullimore.pclock.animsup.messages._
import akka.actor.Actor

case class MsgAnimationState(message: String, x: Int, count: Int)

class MsgAnimation extends Actor {

  override def receive = idle(MsgAnimationState("", 0, 0))
  
  private def idle(state: MsgAnimationState): Receive = {
    case AnimationInit(time, data) =>
      context.become(scrollMessage(MsgAnimationState(data.getOrElse("?"), 48, 0)))
      draw(state)
  }
  
  private def scrollMessage(state: MsgAnimationState): Receive = {
    case Animate(time) =>
      val count = if (state.count == 1) 0 else state.count + 1
      val x = if (count == 0) state.x - 1 else state.x
      if (x < (state.message.length() * -6)) {
        context.become(idle(MsgAnimationState("", 0, 0)))
        sender() ! AnimationEnded()
      } else {
        val newState = MsgAnimationState(state.message, x, count)
        context.become(scrollMessage(newState))
        draw(newState)
      }
  }
  
   private def draw(state: MsgAnimationState) {
      sender() ! Frame(Font5x7.draw(state.x, 4, state.message))
  }
}
