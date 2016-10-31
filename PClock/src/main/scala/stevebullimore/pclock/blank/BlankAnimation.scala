package stevebullimore.pclock.invaders

import akka.actor.Actor
import stevebullimore.pclock.animsup.messages._

class BlankAnimation extends Actor {

  override def receive = {
    case _ =>
      sender() ! Frame(List())
  }
}
