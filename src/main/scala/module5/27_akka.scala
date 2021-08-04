package module5

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.ActorRef
import akka.actor.typed.SpawnProtocol
import akka.actor.typed.{PostStop}

object intro_actors {

  /** Два основных компонента актора:
    * 1. Behaviour
    * 2. Context
    *
    * Для создания можно воспользоваться фабричными методами объекта Behaviours или заэкстендить AbstractBehavior
    */

  object Supervisor {
    def apply(): Behavior[SpawnProtocol.Command] = Behaviors.setup{ ctx =>
        ctx.log.info(ctx.self.toString())
        SpawnProtocol()
    }
  }

  object behaviours_factory_methods {
    
    object Echo {
      def apply(): Behavior[String] = Behaviors.setup{ ctx =>
            
            Behaviors.receiveMessage{
                case msg =>
                    ctx.log.info(msg)
                    Behaviors.same
            }
      }
    }
  }

  object abstract_behaviour {
    object Echo {

      def apply(): Behavior[String] = Behaviors.setup{ ctx =>
         new Echo(ctx)
      }

      class Echo(ctx: ActorContext[String]) extends AbstractBehavior[String](ctx){
          def onMessage(msg: String): Behavior[String] = {
              ctx.log.info(msg)
              this
          }
      }
    }

  }

  /** Реализовать актор, который будет менять свое поведение в ответ на сообщения
    */

  object change_behaviour {

    object Worker {
      
      sealed trait WorkerProtocol
      object WorkerProtocol {
        case object Start extends WorkerProtocol
        case object StandBy extends WorkerProtocol
        case object Stop extends WorkerProtocol
      }

      import WorkerProtocol._

      def apply(): Behavior[WorkerProtocol] = idle()

      def idle(): Behavior[WorkerProtocol] = Behaviors.setup{ ctx =>
            
            Behaviors.receiveMessage{
                case msg @ Start =>
                    ctx.log.info(msg.toString())
                    workInProgress()
                case msg @ StandBy =>
                    ctx.log.info(msg.toString())
                    idle()
                case msg @ Stop =>
                    ctx.log.info(msg.toString())
                    Behaviors.stopped
            }
      }

      def workInProgress(): Behavior[WorkerProtocol] = Behaviors.setup{ ctx =>
              Behaviors.receiveMessage{
                  case msg @ Start => 
                    Behaviors.unhandled
                  case msg @ StandBy => 
                    ctx.log.info("Перехожу в standby")
                    idle()
                  case msg @ Stop =>
                    ctx.log.info("Останавливаюсь")
                    Behaviors.stopped

              }
      }
    }
  }

  /** *
    * 1. Реализовать актор который будет считать полученные им сообщения
    * 2. Доработать актор так, чтобы он мог возвращать текущий Counter
    */

  object handle_state {
    object Counter {

      sealed trait CounterProtocol
      object CounterProtocol {
        final case object Inc extends CounterProtocol
        final case class GetCounter(replyTo: ActorRef[Int])
            extends CounterProtocol
      }

      import CounterProtocol._
      def apply(init: Int): Behavior[CounterProtocol] = inc(init)

      def inc(counter: Int): Behavior[CounterProtocol] = Behaviors.setup{ctx =>
            Behaviors.receiveMessage{
                case Inc => 
                    inc(counter + 1)
                case GetCounter(replyTo) => 
                    replyTo ! counter
                    Behaviors.same
            }
      }
    }
  }

  object task_dispatcher {

    object TaskDispatcher {
      sealed trait TaskDispatcherProtocol
      case class ParseUrl(url: String) extends TaskDispatcherProtocol
      case class Log(str: String) extends TaskDispatcherProtocol

      case class LogResponseWrapper(msg: LogWorker.ResponseProtocol) extends TaskDispatcherProtocol
      case class ParserResponseWrapper(msg: ParseUrlWorker.ResponseProtocol) extends TaskDispatcherProtocol


      def apply(): Behavior[TaskDispatcherProtocol] = Behaviors.setup{ ctx =>

                val adapter1 = ctx.messageAdapter[LogWorker.ResponseProtocol](rs => LogResponseWrapper(rs))
                val adapter2 = ctx.messageAdapter[ParseUrlWorker.ResponseProtocol](rs => ParserResponseWrapper(rs))

                Behaviors.receiveMessage{
                    case ParseUrl(url) =>
                        val ref = ctx.spawn(ParseUrlWorker(), s"ParseWorker-${java.util.UUID.randomUUID.toString()}")
                        ref ! ParseUrlWorker.ParseUrl(url, ???)
                        Behaviors.same
                    case Log(str) => 
                        val ref = ctx.spawn(LogWorker(), s"LogWorker-${java.util.UUID.randomUUID.toString()}")
                        ref ! LogWorker.Log(str, adapter1)
                        Behaviors.same
                    case LogResponseWrapper(LogWorker.LogDone) => 
                        ctx.log.info("Log done")
                        Behaviors.same
                    case ParserResponseWrapper(ParseUrlWorker.ParseDone) => 
                        ctx.log.info("Parse url done")
                        Behaviors.same
                }
        }
    }


    object LogWorker{
        sealed trait LogProtocol
        case class Log(str: String, replyTo: ActorRef[ResponseProtocol]) extends LogProtocol
        
        sealed trait ResponseProtocol
        case object LogDone extends ResponseProtocol

        def apply(): Behavior[LogProtocol] = Behaviors.setup{ ctx =>
                Behaviors.receiveMessage{
                    case Log(str, replyTo) =>
                        ctx.log.info(s"Logger $str")
                        replyTo ! LogDone
                        Behaviors.stopped
                }
        }
    }

    object ParseUrlWorker{
        sealed trait ParseProtocol
        case class ParseUrl(url: String, replyTo: ActorRef[ResponseProtocol]) extends ParseProtocol

        sealed trait ResponseProtocol
        case object ParseDone extends ResponseProtocol

        def apply(): Behavior[ParseProtocol] = Behaviors.setup{ctx =>
            Behaviors.receiveMessage{
                case ParseUrl(url, replyTo) =>
                    ctx.log.info(s"Parsing $url")
                    replyTo ! ParseDone
                    Behaviors.stopped
            }
        }
    }

  }

}