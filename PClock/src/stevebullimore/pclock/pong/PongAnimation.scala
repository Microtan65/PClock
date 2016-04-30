package stevebullimore.pclock.pong

import org.joda.time.DateTime

import stevebullimore.pclock.{ Animation, AnimationEntity, AnimationState, Digit3x5 }

class PongAnimation extends Animation {

  override def animate(state: Option[AnimationState], time: DateTime): (AnimationState, List[(Int, Int)]) = {
    state match {
      case Some(state) => doPong(state, time)
      case None        => doPong(initState(time), time)
    }
  }

  def initState(time: DateTime): AnimationState = List(
    Ball(0, 0, 1, 0.8),
    Score(15, 0, time),
    Paddle(0, 5),
    Paddle(47, 5),
    Net(23, 0))

  def doPong(state: AnimationState, time: DateTime): (AnimationState, List[(Int, Int)]) = {
    val newState = state.map {
      case ball: Ball   => moveBall(ball)
      case score: Score => updateScore(score, time)
      case e            => e
    }

    val pixels = newState.foldLeft(List[(Int, Int)]())((pixels, entity) => entity.draw() ++ pixels)

    (newState, pixels)
  }

  def moveBall(ball: Ball): Ball = {
    val xPos = math.max(0, math.min(ball.xPos + ball.xSpeed, 47))
    val yPos = math.max(0, math.min(ball.yPos + ball.ySpeed, 15))

    val xSpeed =
      if (xPos == 47 || xPos == 0)
        0 - ball.xSpeed
      else
        ball.xSpeed

    val ySpeed = if (yPos == 15 || yPos == 0)
      0 - ball.ySpeed
    else
      ball.ySpeed

    Ball(xPos, yPos, xSpeed, ySpeed)
  }

  def updateScore(score: Score, time: DateTime): Score = {
    if (time.getSecondOfMinute != 4) score
    else Score(score.xPos, score.yPos, time)
  }
}
