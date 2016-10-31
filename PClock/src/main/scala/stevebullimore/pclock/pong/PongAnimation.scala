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
  
  override def receive = animate(List[AnimationEntity]())

  private def animate(state: List[AnimationEntity]): Receive = {
    
      case AnimationInit(time, data) =>
        sender() ! Frame(List())
        context.become(animate(init(time)))

      case Animate(time) =>
        // get all events
        val events = state.foldLeft(List[AnimationEvent]())((events, entity) => entity.getEvents(time) ++ events)
        // update state from time and events
        val newState = state.map(entity => entity.update(events, time))
        // render new state as pixels
        sender() ! Frame(newState.foldLeft(List[(Int, Int)]())((pixels, entity) => entity.draw() ++ pixels))
        
        context.become(animate(newState))
  }
  
  private def init(time: DateTime) = List(
    Ball(0, 0, 1, 0.8, true),
    Score(15, 0, time),
    LeftPaddle(5, 5),
    RightPaddle(5, 5),
    Net(23, 0))
    
    

}
