package stevebullimore.pclock.display

import akka.actor.Actor
import stevebullimore.pclock.animsup.messages._


class LEDDisplay extends Actor {
  private val panel0 = new Sure2416LedPanel(0)
  private val panel1 = new Sure2416LedPanel(1)
    
  override def receive = {
    case Frame(pixels) =>
      SpiWriter.writePanel0(panel0.computeFrame(pixels))
      SpiWriter.writePanel1(panel1.computeFrame(pixels))
  }
}