package stevebullimore.pclock.animsup

import scala.concurrent.duration._
import java.net.InetAddress

import org.joda.time.DateTime
import akka.actor.{Actor, ActorRef, Cancellable, Props}
import messages._
import states._
import stevebullimore.pclock.analyser.AnalyserAnimation
import stevebullimore.pclock.bars.BarsAnimation
import stevebullimore.pclock.blank.BlankAnimation
import stevebullimore.pclock.blocks.BlocksAnimation
import stevebullimore.pclock.clockscroll.TimeAndDateScrollAnimation
import stevebullimore.pclock.clockscroll.TimeScrollAnimation
import stevebullimore.pclock.pong._
import stevebullimore.pclock.invaders._
import stevebullimore.pclock.msg._
import stevebullimore.pclock.rss._
import stevebullimore.pclock.tetris._
import stevebullimore.pclock.display._
import stevebullimore.pclock.unixtime._

class AnimationSupervisor extends Actor {
  import context._
  
  private val startupState = RunningFinite(0, Option(InetAddress.getLocalHost.getHostAddress), None, 3)
  
  private val display = system.actorOf(Props[LEDDisplay])
  
  private val continuousAnims = List[ActorRef](system.actorOf(Props[PongAnimation]), system.actorOf(Props[BlankAnimation]),
    system.actorOf(Props[TimeScrollAnimation]), system.actorOf(Props[TimeAndDateScrollAnimation]), system.actorOf(Props[BarsAnimation]),
    system.actorOf(Props[UnixTimeAnimation]), system.actorOf(Props[TetrisAnimation]), system.actorOf(Props[BlocksAnimation]),
    system.actorOf(Props[AnalyserAnimation]))
  private val finiteAnims = List[ActorRef](system.actorOf(Props[MsgAnimation]), system.actorOf(Props[RssAnimation]))
  
  override def preStart() = {
    startFinite(startupState.id, startupState.data, startupState.freq, startupState.contId)
  }

  override def receive = runningFinite(startupState)
  
  private def runningContinuous(state: RunningContinuous): Receive = {
    case SelectFiniteAnim(id, data, freq) =>
      state.cancelFinite.map(cancelFinite => cancelFinite.cancel)
      startFinite(id, data, freq, state.id)
    
    case SelectContinuousAnim(id) =>
      if (id != state.id) startContinuous(id, state.cancelFinite)
      
    case msg: Any => commonMessages(continuousAnims(state.id))(msg)
  }
  
  private def runningFinite(state: RunningFinite): Receive = {
    case AnimationEnded() =>
      val cancelFinite = state.freq.map { freq =>
        system.scheduler.scheduleOnce(freq seconds, self, SelectFiniteAnim(state.id, state.data, Option(freq))) 
      }
      startContinuous(state.contId, cancelFinite)
      
    case SelectContinuousAnim(id) =>
      context.become(runningFinite(RunningFinite(state.id, state.data, state.freq, id)))
      
    case SelectFiniteAnim(id, data, freq) =>
      
    case msg: Any => commonMessages(finiteAnims(state.id))(msg)
  }
  
  private def startContinuous(id: Int, cancelFinite: Option[Cancellable]) {
    continuousAnims.lift(id).foreach { anim =>
      anim ! AnimationInit(new DateTime(), None)
      context.become(runningContinuous(RunningContinuous(id, cancelFinite)))
    }
  }
  
  private def startFinite(id: Int, data: Option[String], freq: Option[Int], contId: Int) {
   finiteAnims.lift(id).foreach { anim =>
     anim ! AnimationInit(new DateTime(), data)
     context.become(runningFinite(RunningFinite(id, data, freq, contId)))
   }
  }
  
  private def commonMessages(anim: ActorRef): Receive = {
    case f: Frame =>
      if (sender() == anim) {
        display ! f
        system.scheduler.scheduleOnce(15 millis, anim, Animate(new DateTime()))
      }
    case AskAnimations() =>
      sender() ! AnimationInfos(continuousAnims.zipWithIndex.map{case (a, i) => AnimationInfos.AnimationInfo(i, "name", true, a == anim)} ++
        finiteAnims.zipWithIndex.map{case (a, i) => AnimationInfos.AnimationInfo(i, "name", false, a == anim)})
    case b: Brightness =>
      display ! b
  }
}