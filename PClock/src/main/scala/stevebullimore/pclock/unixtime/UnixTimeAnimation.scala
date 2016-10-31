package stevebullimore.pclock.unixtime

import akka.actor.Actor
import stevebullimore.pclock.animsup.messages._
import stevebullimore.pclock.display.Digit3x5

class UnixTimeAnimation extends Actor {
  private val exp = 1000000000

  override def receive(): Receive = {

    case AnimationInit(_, _) =>
      sender() ! Frame(List())

    case Animate(_) =>
      val t = (System.currentTimeMillis / 1000).toInt
      sender() ! Frame(
        drawNumber(4, 1, exp, t, List()) ++
        drawNumber(4, 9, exp, Int.MaxValue - t, List()))
  }

  private def drawNumber(x: Int, y: Int, exp: Int, n: Int, pixels: List[(Int, Int)]): List[(Int, Int)] = {
    if (exp == 0) pixels
    else drawNumber(x + 4, y, exp / 10, n, Digit3x5.draw(x, y, (n / exp) % 10) ++ pixels)
  }
}