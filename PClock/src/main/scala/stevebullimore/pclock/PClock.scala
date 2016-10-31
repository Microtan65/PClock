package stevebullimore.pclock

import stevebullimore.pclock.animsup.AnimationSupervisor
import akka.actor.{ Actor, ActorSystem, Props }
import akka.io.IO
import akka.routing.RoundRobinPool
import spray.can.Http
import stevebullimore.pclock.http.HttpActor

object PClock {
  
  def main(args: Array[String]) {
    implicit val system = ActorSystem("PClockSystem")
    
    // create and start the animation actor
    system.actorOf(Props[AnimationSupervisor], name = "AnimationActor")

    // create and start http service actor
    val httpActor = system.actorOf(RoundRobinPool(5).props(Props[HttpActor]), "HttpActor")

    // start a new HTTP server on port 8080 with our http actor as the handler
    IO(Http) ! Http.Bind(httpActor, "0.0.0.0", port = 8080)
  }
}

