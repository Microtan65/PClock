package stevebullimore.pclock.display

import akka.actor.Actor
import stevebullimore.pclock.animsup.messages._

case class Init()

class LEDDisplay extends Actor {
  private val initCommands = Array[Byte](-128,40,6)
  
  private val panel0 = new Sure2416LedPanel(0)
  private val panel1 = new Sure2416LedPanel(1)
    
  override def preStart() = {
    self ! Init()
  }

  override def receive = {
    case Init() =>
      SpiWriter.writePanel0(initCommands)
      SpiWriter.writePanel1(initCommands)
      
    case Frame(pixels) =>
      SpiWriter.writePanel0(panel0.computeFrame(pixels))
      SpiWriter.writePanel1(panel1.computeFrame(pixels))
  }
}