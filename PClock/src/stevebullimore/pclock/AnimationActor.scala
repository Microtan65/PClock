package stevebullimore.pclock

import akka.actor.Actor
import org.joda.time.DateTime

case class AnimateMessage()
case class ActivateAnimationMessage(idx: Int)
case class AskCurrentAnimationMessage()

class AnimationActor extends Actor {
  private var animationIdx: Int = _
  private var state: AnimationState = _

  import context._
  import scala.concurrent.duration._

  override def preStart() = {
    self ! ActivateAnimationMessage(0)
    system.scheduler.scheduleOnce(500 millis, self, AnimateMessage())
  }
  // override postRestart so we don't call preStart and schedule a new message
  override def postRestart(reason: Throwable) = {}

  def receive = {
    case AnimateMessage() =>
      val (newState, pixels) = PClock.animations(animationIdx).animate(state, new DateTime())

      // side effects to LED panel..
      SpiWriter.writePanel0(PClock.panel0.computeFrame(pixels))
      SpiWriter.writePanel1(PClock.panel1.computeFrame(pixels))

      state = newState

      system.scheduler.scheduleOnce(15 millis, self, AnimateMessage())
      
    case ActivateAnimationMessage(idx) =>
      if ((0 until PClock.animations.length).contains(idx)) {
        animationIdx = idx
        state = PClock.animations(animationIdx).init(new DateTime())
      }
    
    case AskCurrentAnimationMessage() =>
      sender ! animationIdx
  }
}