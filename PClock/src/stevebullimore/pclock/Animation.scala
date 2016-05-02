package stevebullimore.pclock

import org.joda.time.DateTime


trait AnimationEvent

trait AnimationEntity {
  def xPos: Double
  def yPos: Double

  def getEvents(time: DateTime): List[AnimationEvent]
  def update(events: List[AnimationEvent], time: DateTime): AnimationEntity
  def draw(): List[(Int, Int)]
}


trait Animation {
  def init(time: DateTime): AnimationState
  def animate(state: AnimationState, time: DateTime): (AnimationState, List[(Int, Int)]) = {
    
    // get all events
    val events = state.foldLeft(List[AnimationEvent]())((events, entity) => entity.getEvents(time) ++ events)

    // update state from time and events
    val newState = state.map(entity => entity.update(events, time))

    // render new state as pixels
    val pixels = newState.foldLeft(List[(Int, Int)]())((pixels, entity) => entity.draw() ++ pixels)

    (newState, pixels)
    
  }
}