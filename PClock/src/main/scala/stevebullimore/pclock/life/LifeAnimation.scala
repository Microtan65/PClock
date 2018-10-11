package stevebullimore.pclock.life

import ddf.minim.analysis.FFT
import akka.actor.Actor
import ddf.minim.Minim
import stevebullimore.pclock.animsup.messages._
import stevebullimore.pclock.display.Digit3x5
import org.joda.time.DateTime


class LifeAnimation extends Actor {
  import LifeAnimation._

  val random = new scala.util.Random
  override def receive = animate(List[(Int, Int)](), 0)

  private def animate(board: List[(Int, Int)], resetCounter: Int): Receive = {
    case AnimationInit(_, _) =>
      context.become(animate(seedBoard(), 0))
      sender() ! Frame(List())

    case Animate(time) =>
      val newBoard = if (resetCounter == resetSpeed) seedBoard() else evolve(board)

      sender() ! Frame(newBoard.filter { case (x, y) => !(x < 18 && y < 6) } ++ drawTime(time) ++ (if (time.getMillisOfSecond > 500) List((8, 1), (8, 3)) else List()))
      
      context.become(animate(newBoard, if (resetCounter == resetSpeed) 0 else resetCounter + 1))
  }

  private def seedBoard(): List[(Int, Int)] = {
    List.fill(250 + random.nextInt(100))(random.nextInt(48), random.nextInt(16))
  }
  
  private def evolve(board: List[(Int, Int)]): List[(Int, Int)] = {
    def numAlive(x: Int, y: Int): Int = {
      board.foldLeft(0) { (count, pixel) =>
        if ((pixel._1 != x || pixel._2 != y) && (math.abs(pixel._1 - x) <= 1 && math.abs(pixel._2 - y) <= 1)) count + 1 else count
      }
    }

    (0 to 47).foldLeft(List[(Int, Int)]()) { (pixels, x) => (0 to 15).foldLeft(pixels) { 
      (pixels, y) => {
        val n = numAlive(x, y)
        val alive = board.contains((x, y))
        if ((alive && (n == 2 || n == 3)) || (!alive && n == 3))
          (x, y) :: pixels
        else
          pixels
      }}
    }
  }
  
  private def drawTime(time: DateTime): List[(Int, Int)] = {
    Digit3x5.draw(0, 0, time.getHourOfDay / 10) ++
    Digit3x5.draw(4, 0, time.getHourOfDay % 10) ++
    Digit3x5.draw(10, 0, time.getMinuteOfHour / 10) ++
    Digit3x5.draw(14, 0, time.getMinuteOfHour % 10)
  }
}

object LifeAnimation {
  val resetSpeed = 350
}