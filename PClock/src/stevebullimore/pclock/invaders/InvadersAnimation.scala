package stevebullimore.pclock.invaders

import akka.actor.Actor
import org.joda.time.DateTime
import stevebullimore.pclock.animsup.messages._

class InvadersAnimation extends Actor {

  override def receive = {
    case AnimationInit(time, data) =>
      sender() ! Frame(List())
    case Animate(time) =>
      sender() ! Frame(List())
  }
}
