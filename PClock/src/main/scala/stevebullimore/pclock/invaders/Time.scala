package stevebullimore.pclock.invaders

import org.joda.time.DateTime
import stevebullimore.pclock.display.Digit3x5

object Time {
  val x = 10
  val y = 0
  def draw(time: DateTime): List[(Int, Int)] = {
    Digit3x5.draw(x, y, time.getHourOfDay / 10) ++
    Digit3x5.draw(x + 4, y, time.getHourOfDay % 10) ++
    Digit3x5.draw(x + 10, y, time.getMinuteOfHour / 10) ++
    Digit3x5.draw(x + 14, y, time.getMinuteOfHour % 10) ++
    drawMinuteTens(time) ++
    drawMinuteUnits(time)
  }

  private def drawMinuteUnits(time: DateTime): List[(Int, Int)] = {
    if (time.getMillisOfSecond < 900)
      Digit3x5.draw(x + 24, y, time.getSecondOfMinute % 10)
    else
      Digit3x5.drawAsterix(x + 24, y)
  }
  private def drawMinuteTens(time: DateTime): List[(Int, Int)] = {
    if ((time.getSecondOfMinute % 10) == 0 && time.getMillisOfSecond < 400) {
      if (time.getMillisOfSecond > 300)
        Digit3x5.drawAsterix(x + 20, y)
      else {
        val tens = (time.getSecondOfMinute / 10) - 1
        val prevTens = if (tens < 0) 5 else tens
        Digit3x5.draw(x + 20, y, prevTens)
      }
    } else
      Digit3x5.draw(x + 20, y, time.getSecondOfMinute / 10)
  }
}