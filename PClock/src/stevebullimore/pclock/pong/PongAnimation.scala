package stevebullimore.pclock.pong

import org.joda.time.DateTime
import stevebullimore.pclock.{ Animation }

class PongAnimation extends Animation {
  
  override def name = "Pong"

  override def init(time: DateTime) = List(
    Ball(0, 0, 1, 0.8),
    Score(15, 0, time),
    LeftPaddle(5, 5),
    RightPaddle(5, 5),
    Net(23, 0))
}
