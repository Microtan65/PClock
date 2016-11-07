package stevebullimore.pclock.tetris

import org.joda.time.DateTime
import stevebullimore.pclock.animsup.messages._
import akka.actor.Actor

class TetrisAnimation extends Actor {
  import TetrisAnimation._
  
  val shapes = Array(Shape(List((-1, 0), (0, 0), (1, 0), (2, 0))), Shape(List((-1, -1), (-1, 0), (0, 0), (1, 0))),
      Shape(List((-1, 0), (0, -1), (0, 0), (0, 1))), Shape(List((-1, -1), (0, -1), (-1, 0), (0, 0))),
      Shape(List((-1, -1), (0, -1), (0, 0), (1, 0))))

  val digits = Array(Digit(List(ShapePlacement(shapes(0), 0, 7, 1), ShapePlacement(shapes(0), 1, 7, 1), 
      ShapePlacement(shapes(1), 1, 4, 3), ShapePlacement(shapes(1), 0, 3, 1), ShapePlacement(shapes(3), 1, 1, 0))))

  override def receive = {   
      case AnimationInit(time, data) =>
        init()
  }

  def animate(hourTen: DigitInstance, updateCounter: Int): Receive = {
      case AnimationInit(time, data) =>
        init()
        
      case Animate(time) =>
        sender() ! Frame(hourTen.draw())
        if (updateCounter == 5) 
          context.become(animate(hourTen.update(), 0))
        else
          context.become(animate(hourTen, updateCounter+1))
  }
  
  private def init(): Unit = {
        sender() ! Frame(List())
        context.become(animate(DigitInstance(digits(0), 5, digitsY,
            digits(0).shapePlacements.zipWithIndex.map { case(sp, i) => FallingShapePlacement(sp, 4 + sp.x, (0 - digitsY) - (i * 4), sp.rotation) }), 0))
  }
}

object TetrisAnimation {
  val digitsY = 5
}