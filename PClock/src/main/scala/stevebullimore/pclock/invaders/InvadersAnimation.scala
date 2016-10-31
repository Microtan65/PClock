package stevebullimore.pclock.invaders

import akka.actor.Actor
import stevebullimore.pclock.animsup.messages._

class InvadersAnimation extends Actor {

  override def receive = {
    case AnimationInit(time, data) =>
      sender() ! Frame(Time.draw(time))

    case Animate(time) =>
      sender() ! Frame(Time.draw(time))
  }
}
