package stevebullimore.pclock.pong

import org.joda.time.DateTime
import stevebullimore.pclock.animsup.messages._
import akka.actor.Actor

trait AnimationEvent

trait AnimationEntity {
  def xPos: Double
  def yPos: Double

  def getEvents(time: DateTime): List[AnimationEvent]
  def update(events: List[AnimationEvent], time: DateTime): AnimationEntity
  def draw(): List[(Int, Int)]
}

class PongAnimation extends Actor {
  
  private var state = List[AnimationEntity]()
  
  private def animate(time: DateTime) {
    // get all events
    val events = state.foldLeft(List[AnimationEvent]())((events, entity) => entity.getEvents(time) ++ events)

    // update state from time and events
    val newState = state.map(entity => entity.update(events, time))

    // render new state as pixels
    val pixels = newState.foldLeft(List[(Int, Int)]())((pixels, entity) => entity.draw() ++ pixels)
    
    state = newState

    sender() ! Frame(pixels)
  }
  
  def init(time: DateTime) = List(
    Ball(0, 0, 1, 0.8),
    Score(15, 0, time),
    LeftPaddle(5, 5),
    RightPaddle(5, 5),
    Net(23, 0))
    
    
  override def receive = {
    case AnimationInit(time, data) =>
      state = init(time)
      animate(time)
    case Animate(time) =>
      animate(time)
  }
}
