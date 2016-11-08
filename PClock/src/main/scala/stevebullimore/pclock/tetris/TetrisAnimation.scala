package stevebullimore.pclock.tetris

import scala.util.Random
import org.joda.time.DateTime
import stevebullimore.pclock.animsup.messages._
import akka.actor.Actor

class TetrisAnimation extends Actor {
  import TetrisAnimation._
  
  val shapes = Array(Shape(List((-1, 0), (0, 0), (1, 0), (2, 0))), Shape(List((-1, -1), (-1, 0), (0, 0), (1, 0))),
      Shape(List((-1, 0), (0, 0), (1, 0), (0, -1))), Shape(List((-1, -1), (0, -1), (-1, 0), (0, 0))),
      Shape(List((-1, -1), (0, -1), (0, 0), (1, 0))), Shape(List((-1, 0), (-1, 1), (0, 0), (1, 0))),
      Shape(List((-1, 0), (0, 0), (0, -1), (1, -1))))

  val digits = Array(
      // 0
      Digit(0, List(ShapePlacement(shapes(1), 1, 9, 0), ShapePlacement(shapes(2), 3, 8, 2), 
      ShapePlacement(shapes(1), 5, 8, 3), ShapePlacement(shapes(0), 0, 5, 1), ShapePlacement(shapes(4), 4, 6, 1),
      ShapePlacement(shapes(0), 1, 6, 1), ShapePlacement(shapes(0), 4, 3, 1), ShapePlacement(shapes(2), 1, 3, 3),
      ShapePlacement(shapes(0), 5, 2, 1), ShapePlacement(shapes(5), 1, 1, 0), ShapePlacement(shapes(6), 4, 1, 0),
      ShapePlacement(shapes(0), 1, 0, 0))),
      // 1
      Digit(1, List(ShapePlacement(shapes(0), 2, 7, 1), ShapePlacement(shapes(0), 3, 7, 1), 
      ShapePlacement(shapes(1), 3, 4, 3), ShapePlacement(shapes(1), 2, 3, 1), ShapePlacement(shapes(3), 3, 1, 0))),
      // 2
      Digit(2, List(ShapePlacement(shapes(0), 2, 9, 0), ShapePlacement(shapes(5), 1, 8, 0), 
      ShapePlacement(shapes(1), 4, 8, 2), ShapePlacement(shapes(3), 1, 7, 0), ShapePlacement(shapes(0), 1, 5, 0),
      ShapePlacement(shapes(0), 1, 4, 0), ShapePlacement(shapes(1), 5, 4, 3), ShapePlacement(shapes(1), 4, 3, 1),
      ShapePlacement(shapes(5), 4, 1, 2), ShapePlacement(shapes(5), 3, 0, 0), ShapePlacement(shapes(3), 1, 1, 0))),
      // 3
      Digit(3, List(ShapePlacement(shapes(1), 1, 9, 0), ShapePlacement(shapes(5), 4, 9, 2), 
      ShapePlacement(shapes(0), 2, 8, 0), ShapePlacement(shapes(3), 5, 7, 0), ShapePlacement(shapes(1), 5, 4, 3),
      ShapePlacement(shapes(3), 3, 5, 0), ShapePlacement(shapes(0), 4, 2, 1), ShapePlacement(shapes(5), 5, 1, 1),
      ShapePlacement(shapes(5), 2, 1, 2), ShapePlacement(shapes(5), 1, 0, 0))),
      // 4
      Digit(4, List(ShapePlacement(shapes(3), 5, 9, 0), ShapePlacement(shapes(3), 5, 7, 0), 
      ShapePlacement(shapes(1), 5, 4, 3), ShapePlacement(shapes(0), 4, 2, 1), ShapePlacement(shapes(0), 1, 5, 0),
      ShapePlacement(shapes(0), 1, 4, 0), ShapePlacement(shapes(5), 5, 1, 1), ShapePlacement(shapes(1), 1, 2, 3),
      ShapePlacement(shapes(1), 0, 1, 1))),
      // 5
      Digit(5, List(ShapePlacement(shapes(3), 5, 9, 0), ShapePlacement(shapes(1), 1, 9, 0), ShapePlacement(shapes(1), 2, 8, 2), 
      ShapePlacement(shapes(0), 4, 5, 1), ShapePlacement(shapes(0), 5, 5, 1), ShapePlacement(shapes(5), 2, 5, 2),
      ShapePlacement(shapes(5), 1, 4, 0), ShapePlacement(shapes(3), 1, 3, 0), ShapePlacement(shapes(1), 1, 1, 0),
      ShapePlacement(shapes(5), 4, 1, 2), ShapePlacement(shapes(0), 2, 0, 0))),
      // 6
      Digit(6, List(ShapePlacement(shapes(2), 1, 9, 0), ShapePlacement(shapes(1), 5, 8, 3), ShapePlacement(shapes(2), 3, 8, 2), 
      ShapePlacement(shapes(4), 0, 7, 1), ShapePlacement(shapes(2), 4, 6, 1), ShapePlacement(shapes(2), 0, 5, 1),
      ShapePlacement(shapes(4), 2, 5, 0), ShapePlacement(shapes(1), 4, 4, 2), ShapePlacement(shapes(3), 1, 3, 0),
      ShapePlacement(shapes(0), 2, 1, 0), ShapePlacement(shapes(5), 1, 0, 0), ShapePlacement(shapes(1), 4, 0, 2))),
      // 7
      Digit(7, List(ShapePlacement(shapes(3), 5, 9, 0), ShapePlacement(shapes(5), 4, 6, 3), ShapePlacement(shapes(5), 5, 6, 1), 
      ShapePlacement(shapes(3), 5, 3, 0), ShapePlacement(shapes(5), 4, 1, 2), ShapePlacement(shapes(1), 1, 1, 0),
      ShapePlacement(shapes(0), 2, 0, 0))),
      // 8
      Digit(8, List(ShapePlacement(shapes(2), 1, 9, 0), ShapePlacement(shapes(1), 5, 8, 3), ShapePlacement(shapes(2), 3, 8, 2), 
      ShapePlacement(shapes(4), 0, 7, 1), ShapePlacement(shapes(2), 4, 6, 1), ShapePlacement(shapes(2), 0, 5, 1),
      ShapePlacement(shapes(4), 2, 5, 0), ShapePlacement(shapes(1), 4, 4, 2), ShapePlacement(shapes(3), 1, 3, 0),
      ShapePlacement(shapes(3), 5, 3, 0), ShapePlacement(shapes(0), 2, 1, 0), ShapePlacement(shapes(5), 1, 0, 0), 
      ShapePlacement(shapes(1), 4, 0, 2))),
      // 9
      Digit(9, List(ShapePlacement(shapes(3), 5, 9, 0), ShapePlacement(shapes(1), 1, 9, 0), ShapePlacement(shapes(1), 2, 8, 2), 
      ShapePlacement(shapes(0), 4, 5, 1), ShapePlacement(shapes(0), 5, 5, 1), ShapePlacement(shapes(5), 2, 5, 2),
      ShapePlacement(shapes(5), 1, 4, 0), ShapePlacement(shapes(3), 1, 3, 0), ShapePlacement(shapes(3), 5, 3, 0),
      ShapePlacement(shapes(1), 1, 1, 0), ShapePlacement(shapes(5), 4, 1, 2), ShapePlacement(shapes(0), 2, 0, 0)))
      )

  override def receive = {   
      case AnimationInit(time, _) =>
        init(time)
  }

  def animate(hourTen: DigitInstance, hourUnit: DigitInstance, minTen: DigitInstance, minUnit: DigitInstance, updateCounter: Int): Receive = {
      case AnimationInit(time, data) =>
        init(time)

      case Animate(time) =>
        val minUnitToUpdate = time.getSecondOfMinute >= 58
        val minTenToUpdate = minUnitToUpdate && time.getMinuteOfHour % 10 == 9
        val hourUnitToUpdate = minTenToUpdate && time.getMinuteOfHour == 59
        val hourTenToUpdate = hourUnitToUpdate && (time.getHourOfDay % 10 == 9 || time.getHourOfDay == 23)
        val flashState = time.getMillisOfSecond % 100 > 50

        sender() ! Frame(
            (if (hourTenToUpdate && flashState) List() else hourTen.draw()) ++ 
            (if (hourUnitToUpdate && flashState) List() else hourUnit.draw()) ++ 
            (if (minTenToUpdate && flashState) List() else minTen.draw()) ++ 
            (if (minUnitToUpdate && flashState) List() else minUnit.draw()) ++ 
            drawSeperator(time))

        if (updateCounter == 5) 
          context.become(animate(hourTen.update(), hourUnit.update(), minTen.update(), minUnit.update(), 0))
        else
          context.become(animate(
              if (time.getHourOfDay/10 != hourTen.digit.id) createDigitInstance(time.getHourOfDay/10, digitsX) else hourTen, 
              if (time.getHourOfDay%10 != hourUnit.digit.id) createDigitInstance(time.getHourOfDay%10, digitsX + 8) else hourUnit, 
              if (time.getMinuteOfHour/10 != minTen.digit.id) createDigitInstance(time.getMinuteOfHour/10, digitsX + 8 + 8 + 4) else minTen, 
              if (time.getMinuteOfHour%10 != minUnit.digit.id) createDigitInstance(time.getMinuteOfHour%10, digitsX + 8 + 8 + 4 + 8) else minUnit, updateCounter+1))
  }

  private def drawSeperator(time: DateTime): List[(Int, Int)] = {
    val ms = time.getMillisOfSecond
    if (ms < 500 || (ms < 800 && (ms % 100) > 40)) 
      List((digitsX + 16, digitsY + 2), (digitsX + 16, digitsY + 3), (digitsX + 17, digitsY + 2), (digitsX + 17, digitsY + 3),
           (digitsX + 16, digitsY + 6), (digitsX + 16, digitsY + 7), (digitsX + 17, digitsY + 6), (digitsX + 17, digitsY + 7))
    else
      List()
  }

  private def init(time: DateTime): Unit = {
        sender() ! Frame(List())
        context.become(animate(
            createDigitInstance(time.getHourOfDay/10, digitsX), 
            createDigitInstance(time.getHourOfDay%10, digitsX + 8), 
            createDigitInstance(time.getMinuteOfHour/10, digitsX + 8 + 8 + 4), 
            createDigitInstance(time.getMinuteOfHour%10, digitsX + 8 + 8 + 4 + 8), 0))
  }
  
  private def createDigitInstance(digit: Int, x: Int): DigitInstance = {
    DigitInstance(digits(digit), x, digitsY,
       digits(digit).shapePlacements.zipWithIndex.map { case(sp, i) => FallingShapePlacement(sp, sp.x + (Random.nextInt(7) - 3), (0 - digitsY) - (i * 6), Random.nextInt(4)) })
  }
}

object TetrisAnimation {
  val digitsY = 6
  val digitsX = 7
}