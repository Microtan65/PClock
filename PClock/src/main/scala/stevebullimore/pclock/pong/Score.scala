package stevebullimore.pclock.pong

import org.joda.time.DateTime
import stevebullimore.pclock.display.Digit3x5

case class Score(xPos: Double, yPos: Double, time: DateTime) extends AnimationEntity {
  override def getEvents(latestTime: DateTime): List[AnimationEvent] = {
    if (latestTime.getSecondOfMinute == 0) List(NewGame())
    else List()
  }
  override def update(events: List[AnimationEvent], time: DateTime): AnimationEntity = {
    if (events.contains(NewGame())) Score(xPos, yPos, time)
    else this
  }
  override def draw(): List[(Int, Int)] = {
    val x = xPos.toInt
    val y = yPos.toInt
    Digit3x5.draw(x, y, time.getHourOfDay / 10) ++
    Digit3x5.draw(x + 4, y, time.getHourOfDay % 10) ++
    Digit3x5.draw(x + 10, y, time.getMinuteOfHour / 10) ++
    Digit3x5.draw(x + 14, y, time.getMinuteOfHour % 10)
  }
}