package stevebullimore.pclock

import org.joda.time.DateTime

trait AnimationEntity {
  def xPos: Double
  def yPos: Double
  
  def draw(): List[(Int, Int)]
}

trait Animation {
  def animate(state: Option[AnimationState], time: DateTime): (AnimationState, List[(Int, Int)])
}