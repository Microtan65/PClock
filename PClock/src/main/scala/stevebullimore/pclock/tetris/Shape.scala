package stevebullimore.pclock.tetris

case class Shape(pixels: List[(Int, Int)]) {
  // rotation matrices of 90deg, 180deg, 270deg
  val rotations = Array((0, -1), (1, 0)) :: Array((-1, 0), (0, -1)) :: Array((0, 1), (-1, 0)) :: Nil

  def rotate(rotation: Int): List[(Int, Int)] = {
    rotations.lift(rotation - 1).map { rot =>
      pixels.map { case (x, y) => (x*rot(0)._1 + y*rot(0)._2, x*rot(1)._1 + y*rot(1)._2) }
    } getOrElse(pixels)
  }
}

case class ShapePlacement(shape: Shape, x: Int, y: Int, rotation: Int) {
}

case class FallingShapePlacement(placement: ShapePlacement, cx: Int, cy: Int, cr: Int) {
  def draw(): List[(Int, Int)] = {
    placement.shape.rotate(cr).map { case(x1, y1) => (x1 + cx, y1 + cy) }
  }
}

case class Digit(shapePlacements: List[ShapePlacement]) {
}

case class DigitInstance(digit: Digit, x: Int, y: Int, fallingShapePlacements: List[FallingShapePlacement]) {
  import DigitInstance._

  def draw(): List[(Int, Int)] = {
    fallingShapePlacements.foldLeft(List[(Int, Int)]())((pixels, fsp) => pixels ++ fsp.draw()).map {
      case (x1, y1) => (x1 + x, y1 + y) 
    }
  }
  def update(): DigitInstance = {
    DigitInstance(digit, x, y, fallingShapePlacements.map { fsp => 
      val cy = if (fsp.cy < fsp.placement.y) fsp.cy + 1 else fsp.cy
      FallingShapePlacement(fsp.placement, fsp.cx, cy, fsp.cr) })
  }
}
