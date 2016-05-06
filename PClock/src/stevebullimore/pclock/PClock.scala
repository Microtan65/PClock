package stevebullimore.pclock

import scala.annotation.tailrec
import stevebullimore.pclock.pong.PongAnimation
import stevebullimore.pclock.pong.InvadersAnimation
import org.joda.time.DateTime
import akka.actor.{ Actor, ActorSystem, Props }
import akka.io.IO
import akka.routing.RoundRobinPool
import spray.can.Http

object PClock {
  val animations = List[Animation](new PongAnimation(), new InvadersAnimation())
  val panel0 = new Sure2416LedPanel(0)
  val panel1 = new Sure2416LedPanel(1)

  def main(args: Array[String]) {
    implicit val system = ActorSystem("PClockSystem")
    
    // create and start the animation actor
    system.actorOf(Props[AnimationActor], name = "AnimationActor")

    // create and start http service actor
    val httpActor = system.actorOf(RoundRobinPool(5).props(Props[HttpActor]), "HttpActor")

    // start a new HTTP server on port 8080 with our http actor as the handler
    IO(Http) ! Http.Bind(httpActor, "0.0.0.0", port = 8080)
  }
}

