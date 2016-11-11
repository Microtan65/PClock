package stevebullimore.pclock.tetris

import scala.util.Random

case class Shape(pixels: List[(Int, Int)]) {
  // rotation matrices of 90deg, 180deg, 270deg
  val rotations = Array((0, -1), (1, 0)) :: Array((-1, 0), (0, -1)) :: Array((0, 1), (-1, 0)) :: Nil

  def rotate(rotation: Int): List[(Int, Int)] = {
    rotations.lift(rotation - 1).map { rot =>
      pixels.map { case (x, y) => (x*rot(0)._1 + y*rot(0)._2, x*rot(1)._1 + y*rot(1)._2) }
    } getOrElse(pixels)
  }
}

case class ShapePlacement(shape: Shape, x: Int, y: Int, rotation: Int)

case class FallingShapePlacement(placement: ShapePlacement, cx: Int, cy: Int, cr: Int) {
  def draw(): List[(Int, Int)] = {
    placement.shape.rotate(cr).map { case(x1, y1) => (x1 + cx, y1 + cy) }
  }
}

object FallingShapePlacement {
  val random = new Random(System.currentTimeMillis())
  
  def randomPositionAndRotation(placement: ShapePlacement, y: Int, i: Int): FallingShapePlacement = {
    FallingShapePlacement(placement, placement.x + (random.nextInt(7) - 3), (0 - y) - (i * 6), random.nextInt(4))
  }
}

case class Digit(id: Int, shapePlacements: List[ShapePlacement])

case class DigitInstance(digit: Digit, x: Int, y: Int, fallingShapePlacements: List[FallingShapePlacement]) {
  def draw(): List[(Int, Int)] = {
    fallingShapePlacements.foldLeft(List[(Int, Int)]())((pixels, fsp) => pixels ++ fsp.draw()).map {
      case (x1, y1) => (x1 + x, y1 + y) 
    }
  }
  def update(): DigitInstance = {
    DigitInstance(digit, x, y, fallingShapePlacements.map { fsp => 
      val cy = if (fsp.cy < fsp.placement.y) fsp.cy + 1 else fsp.cy
      val cx = if (fsp.cx == fsp.placement.x) fsp.cx else {
        if (fsp.placement.y - fsp.cy < (Math.abs(fsp.cx - fsp.placement.x)+2)) {
          if (fsp.cx > fsp.placement.x) fsp.cx - 1 else fsp.cx + 1
        } else fsp.cx
      }
      val cr = if (fsp.placement.rotation == fsp.cr) fsp.cr else {
        if (fsp.placement.y - fsp.cy > 4) fsp.cr else {
          if (fsp.cr > fsp.placement.rotation) fsp.cr - 1 else fsp.cr + 1
        }
      }
      FallingShapePlacement(fsp.placement, cx, cy, cr) })
  }
}
