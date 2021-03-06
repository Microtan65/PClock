package stevebullimore.pclock.display

import scala.concurrent.duration._
import java.awt.{Color, Graphics}
import javax.swing.JFrame
import javax.swing.JPanel

import akka.actor.Actor
import stevebullimore.pclock.animsup.messages._

case class Init()
case class Brightness(levelPercent: Int)

class LEDDisplay extends Actor {
  import context.dispatcher

  private val initCommandsMaster = List(Array[Byte](-128,0),Array[Byte](-124,-128),Array[Byte](-125,0),Array[Byte](-128,32),Array[Byte](-128,96))
  private val initCommandsSlave = List(Array[Byte](-128,0),Array[Byte](-124,-128),Array[Byte](-126,-128),Array[Byte](-128,32),Array[Byte](-128,96))
  private val panel0 = new Sure2416LedPanel(0)
  private val panel1 = new Sure2416LedPanel(1)

  override def preStart() = {
    self ! Init()
    self ! Brightness(50)
    // the LED panels occasionally stop responding and need another initialisation command to make them
    // responsive again so we send an init sequence periodically
    context.system.scheduler.schedule(60 seconds, 60 seconds, self, Init())
  }

  override def receive = {
    case Init() =>
      initCommandsMaster.foreach{ cmd => SpiWriter.writePanel0(cmd) }
      initCommandsSlave.foreach{ cmd => SpiWriter.writePanel1(cmd) }

    case Brightness(levelPercent) =>
      val level = (levelPercent * 15) / 100
      val byte1Bin = "1001010" + (level/8).toBinaryString
      val byte2Bin = ((level%16) << 5).toBinaryString
      val cmds = Array[Byte](Integer.parseInt(byte1Bin, 2).asInstanceOf[Byte], Integer.parseInt(byte2Bin, 2).asInstanceOf[Byte])
      SpiWriter.writePanel0(cmds)
      SpiWriter.writePanel1(cmds)

    case Frame(pixels) =>
      SpiWriter.writePanel0(panel0.computeFrame(pixels))
      SpiWriter.writePanel1(panel1.computeFrame(pixels))
  }
}

class WindowDisplay extends Actor {
  import WindowDisplay._
  class Surface extends JPanel {
    var pixels: List[(Int, Int)] = List()
    override def paintComponent(g: Graphics): Unit = {
      g.clearRect(0, 0, width, height)
      pixels.foreach { case (x, y) => g.fillOval(x*ballSize, y*ballSize, ballSize, ballSize) }
    }
  }
  val frame: JFrame = new JFrame
  val panel: Surface = new Surface

  override def preStart() = {
    panel.setBackground(Color.WHITE)
    panel.setForeground(Color.RED)
    frame.add(panel)
    frame.setSize(width+20, height+50)
    frame.setVisible(true)
  }

  override def receive = {
    case Frame(pixels) =>
      panel.pixels = pixels
      panel.repaint()
  }
}

object WindowDisplay {
  val ballSize = 10
  val width = ballSize * 48
  val height = ballSize * 16
}