package stevebullimore.pclock

import scala.annotation.tailrec
import stevebullimore.pclock.pong.PongAnimation
import org.joda.time.DateTime

object PClock {
  val animations = List[Animation](new PongAnimation())
  val panel0 = new Sure2416LedPanel(0)
  val panel1 = new Sure2416LedPanel(1)

  def main(args: Array[String]) {
    val animation = animations(0)
    
    @tailrec
    def animationLoop(state: AnimationState): Unit = {
      val (newState, pixels) = animation.animate(state, new DateTime())

      // side effects to LED panel..
      SpiWriter.writePanel0(panel0.computeFrame(pixels))
      SpiWriter.writePanel1(panel1.computeFrame(pixels))
      
      Thread.sleep(20)

      animationLoop(newState)
    }

    animationLoop(animation.init(new DateTime()))
  }
}