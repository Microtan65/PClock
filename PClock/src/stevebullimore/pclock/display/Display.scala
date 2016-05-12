package stevebullimore.pclock.display

import scala.concurrent.duration._
import akka.actor.Actor
import stevebullimore.pclock.animsup.messages._

case class Init()

class LEDDisplay extends Actor {
  import context.dispatcher
  
  private val initCommands = List(Array[Byte](-128,0),Array[Byte](-124,-128),Array[Byte](-126,-128),Array[Byte](-125,0),Array[Byte](-128,32),Array[Byte](-128,96),Array[Byte](-108,-64))
  private val panel0 = new Sure2416LedPanel(0)
  private val panel1 = new Sure2416LedPanel(1)
    
  override def preStart() = {
    self ! Init()
    // the LED panels occasionally stop responding and need another initialisation command to make them
    // responsive again so we send an init sequence periodically
    context.system.scheduler.schedule(60 seconds, 60 seconds, self, Init())
  }

  override def receive = {
    case Init() =>
      initCommands.foreach{ cmd =>
        SpiWriter.writePanel0(cmd)
        SpiWriter.writePanel1(cmd)
      }
      
    case Frame(pixels) =>
      SpiWriter.writePanel0(panel0.computeFrame(pixels))
      SpiWriter.writePanel1(panel1.computeFrame(pixels))
  }
}