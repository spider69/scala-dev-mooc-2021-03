package module5

import akka.actor.typed.SpawnProtocol
import akka.actor.typed.ActorSystem
import scala.concurrent.ExecutionContext
import akka.util.Timeout
import scala.concurrent.duration._
import akka.actor.typed.scaladsl.AskPattern._
import scala.concurrent.Future
import scala.language.postfixOps
import akka.actor.typed.ActorRef
import akka.actor.typed.Props


object AkkaMain {
  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem[SpawnProtocol.Command](intro_actors.Supervisor(), "Echo")
    implicit val timeout: Timeout = Timeout(3 seconds)
    implicit val ec = system.executionContext

    val echo: Future[ActorRef[String]] = system.ask(SpawnProtocol.Spawn(intro_actors.behaviours_factory_methods.Echo(), "Echo", Props.empty, _))

    for(ref <- echo){
        ref ! "Hello"
    }

    Thread.sleep(3000)
    system.terminate()

  }
}
