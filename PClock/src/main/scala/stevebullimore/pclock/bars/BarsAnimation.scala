package stevebullimore.pclock.bars

import akka.actor.Actor
import stevebullimore.pclock.animsup.messages._

class BarsAnimation extends Actor {

  override def receive(): Receive = {

    case AnimationInit(_, _) =>
      sender() ! Frame(List())

    case Animate(time) =>
      sender() ! Frame(
        drawBar(0, 1, 0, (144 * ((time.getHourOfDay * 60) + time.getMinuteOfHour)) / 1440, List((11, 0), (23, 0), (35, 0))) ++
        drawBar(0, 6, 0, (144 * ((time.getMinuteOfHour * 60) + time.getSecondOfMinute)) / 3600, List()) ++
        drawBar(0, 11, 0, (144 * ((time.getSecondOfMinute * 1000) + time.getMillisOfSecond)) / 60000, List()))
  }

  private def drawBar(x: Int, y: Int, z: Int, n: Int, pixels: List[(Int, Int)]): List[(Int, Int)] = {
    if (n == 0) pixels
    else drawBar(x + ((z + 1) / 3), y, (z + 1) % 3, n - 1, (x, y + z) :: pixels)
  }
}