package stevebullimore.pclock.clockscroll

import akka.actor.{Actor, ActorRef, Props}
import org.joda.time.DateTime
import stevebullimore.pclock.animsup.messages._
import stevebullimore.pclock.display.Digit3x5

class TimeAndDateScrollAnimation extends Actor {

  val timeScrollAnimation = context.system.actorOf(Props[TimeScrollAnimation])
  
  override def receive() = awaitAnimate()

  private def awaitAnimate(): Receive = {
    case AnimationInit(time, _) =>
      timeScrollAnimation ! AnimationInit(time, Some("0"))
      context.become(awaitFrame(sender(), time))

    case animate: Animate =>
      timeScrollAnimation ! animate
      context.become(awaitFrame(sender(), animate.time))
  }

  private def awaitFrame(sender: ActorRef, time: DateTime): Receive = {
    case Frame(pixels) =>
      sender ! Frame(pixels ++ drawDate(time))
      context.become(awaitAnimate())
  }

  private def drawDate(time: DateTime): List[(Int, Int)] = {
    val x = 6
    Digit3x5.draw(x, 10, time.getDayOfMonth / 10) ++
    Digit3x5.draw(x+4, 10, time.getDayOfMonth % 10) ++
    Digit3x5.draw(x+10, 10, time.getMonthOfYear / 10) ++
    Digit3x5.draw(x+14, 10, time.getMonthOfYear % 10) ++
    Digit3x5.draw(x+20, 10, time.getYear / 1000) ++
    Digit3x5.draw(x+24, 10, (time.getYear / 100) % 10) ++
    Digit3x5.draw(x+28, 10, (time.getYear / 10) % 10) ++
    Digit3x5.draw(x+32, 10, time.getYear % 10)
  }
}
