package stevebullimore.pclock.clockscroll

import akka.actor.Actor
import org.joda.time.DateTime
import stevebullimore.pclock.animsup.messages._
import stevebullimore.pclock.display.Digit3x5

class TimeScrollAnimation extends Actor {
  import TimeScrollAnimation._

  override def receive = showingTime(5)

  private def showingTime(y: Int): Receive = {

    case AnimationInit(time, data) =>
      sender() ! Frame(List())
      context.become(showingTime(Integer.parseInt(data.getOrElse("5"))))

    case Animate(time) =>
      val secUnitsChange = true
      val secTensChange = time.getSecondOfMinute % 10 == 0
      val minUnitsChange = time.getSecondOfMinute == 0
      val minTensChange = time.getMinuteOfHour % 10 == 0 && minUnitsChange
      val hrUnitsChange = time.getMinuteOfHour == 0 && minTensChange
      val hrTensChange = (time.getHourOfDay % 10 == 0 || time.getHourOfDay == 23) && hrUnitsChange

      sender() ! Frame(
        drawScrollingDigit(x, y, hrTensChange, time.getMillisOfSecond, time.getHourOfDay / 10, 2) ++
        drawScrollingDigit(x + 4, y, hrUnitsChange, time.getMillisOfSecond, time.getHourOfDay % 10, 9) ++
        drawScrollingDigit(x + 10, y, minTensChange, time.getMillisOfSecond, time.getMinuteOfHour / 10, 5) ++
        drawScrollingDigit(x + 14, y, minUnitsChange, time.getMillisOfSecond, time.getMinuteOfHour % 10, 9) ++
        drawScrollingDigit(x + 20, y, secTensChange, time.getMillisOfSecond, time.getSecondOfMinute / 10, 5) ++
        drawScrollingDigit(x + 24, y, secUnitsChange, time.getMillisOfSecond, time.getSecondOfMinute % 10, 9) ++
        drawColons(time.getMillisOfSecond, y))
  }

  private def drawScrollingDigit(xPos: Int, yPos: Int, changing: Boolean, millisInSec: Long, digit: Int, maxDigit: Int): List[(Int, Int)] = {
    if (changing && millisInSec < 250) {
      val yShift = millisInSec / 50
      val prevDigit = if (digit == 0) maxDigit else digit - 1
      val pixels = Digit3x5.draw(xPos, yPos - yShift.toInt, prevDigit) ++ Digit3x5.draw(xPos, (yPos + 6) - yShift.toInt, digit)
      pixels.filter{case (_, y1) => y1 >= yPos && y1 <= yPos+5}
    } else
      Digit3x5.draw(xPos, yPos, digit)
  }
  private def drawColons(millisInSec: Long, y: Int): List[(Int, Int)] = {
    if (millisInSec > 500) List()
    else List((x + 8, y + 1), (x + 8, y + 3), (x + 18, y + 1), (x + 18, y + 3))
  }
}

object TimeScrollAnimation {
  val x = 10
}
