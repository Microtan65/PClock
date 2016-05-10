package stevebullimore.pclock.animsup

import scala.concurrent.duration._
import org.joda.time.DateTime
import akka.actor.{Actor, ActorRef, Props}
import messages._
import states._
import stevebullimore.pclock.pong._
import stevebullimore.pclock.invaders._
import stevebullimore.pclock.msg._
import stevebullimore.pclock.display.LEDDisplay

class AnimationSupervisor extends Actor{
  import context._
  
  private val display = system.actorOf(Props[LEDDisplay])
  
  private val continuousAnims = Array[ActorRef](system.actorOf(Props[PongAnimation]), system.actorOf(Props[InvadersAnimation]))
  private val finiteAnims = Array[ActorRef](system.actorOf(Props[MsgAnimation]))
  
  override def receive = runningContinuous(RunningContinuous(0))
  
  private def runningContinuous(state: RunningContinuous): Receive = {
    case SelectFiniteAnim(id, data, freq) =>
      startFinite(id, data, freq, state.id)
    
    case SelectContinuousAnim(id) =>
      startContinuous(id)
      
    case msg: Any => commonMessages(msg, continuousAnims(state.id))
  }
  
  private def runningFinite(state: RunningFinite): Receive = {
    case AnimationEnded() =>
      state.freq.foreach { 
        freq => system.scheduler.scheduleOnce(freq seconds, self, SelectFiniteAnim(state.id, state.data, Option(freq))) 
      }
      startContinuous(state.contId)
      
    case SelectContinuousAnim(id) =>
      context.become(runningFinite(RunningFinite(state.id, state.data, state.freq, id)))
      
    case SelectFiniteAnim(id, data, freq) =>
      
    case msg: Any => commonMessages(msg, finiteAnims(state.id))
  }
  
  private def startContinuous(id: Int) {
    context.become(runningContinuous(RunningContinuous(id)))
    continuousAnims(id) ! AnimationInit(new DateTime(), None)     
  }
  
  private def startFinite(id: Int, data: Option[String], freq: Option[Int], contId: Int) {
   context.become(runningFinite(RunningFinite(id, data, freq, contId)))
   finiteAnims(id) ! AnimationInit(new DateTime(), data)     
  }
  
  private def commonMessages(msg: Any, anim: ActorRef) = {
    msg match {
    case f: Frame =>
      if (sender() == anim) {
        display ! f
        system.scheduler.scheduleOnce(15 millis, anim, Animate(new DateTime()))
      }
    }
  }
}