package module3

import zio.UIO
import java.util.concurrent.atomic.AtomicReference
import zio.ZIO
import zio.Chunk
import zio.Schedule
import scala.language.postfixOps
import zio.duration.durationInt
import zio.console
import zio.clock.Clock
import zio.random._
import zio.console._
import zio.duration.Duration
import zio.Promise

object zioDS {

  object schedule {

    val eff = putStrLn("Hello")

    /** 1. Написать эффект, котрый будет выводить в консоль Hello 5 раз
      */

    val schedule1 = Schedule.recurs(5)
    val repeat5 = eff.repeat(schedule1)

    /** 2. Написать эффект, который будет выводить в консоль Hello 5 раз, раз в секунду
      */

    val every1Seconds = Schedule.spaced(1 second)
    val every1SecondEff = eff.repeat(every1Seconds && schedule1)

    /** Написать эффект, который будет генерить произвольное число от 0 до 10,
      * и повторяться пока число не будет равным 0
      */
    val random = nextIntBetween(0, 11)
    val schedule2: Schedule[Console, Int, Int] =
      Schedule.recurWhile[Int](_ > 0).tapOutput(i => putStrLn(i.toString))
    val randomSchedule = random.repeat(schedule2)

    /** Написать эффект, который будет выполняться каждую пятницу 12 часов дня
      */

      val schedule = Schedule.dayOfWeek(5) && Schedule.hourOfDay(12)

  }

  object ref {

    trait Ref[A] {
      def modify[B](f: A => (B, A)): UIO[B]

      def get: UIO[A] = modify(a => (a, a))

      def set(a: A): UIO[Unit] = modify(_ => ((), a))

      def update[B](f: A => A): UIO[Unit] =
        modify(a => ((), f(a)))
    }

    object Ref {
      def make[A](a: A): UIO[Ref[A]] = UIO.effectTotal{
        new Ref[A]{
          val atomic = new AtomicReference(a) 
          def modify[B](f: A => (B, A)): UIO[B] = UIO.effectTotal{
            var l = true
            var b: B = null.asInstanceOf[B]
            while(l){
              val current = atomic.get
              val tuple = f(current)
              b = tuple._1
              l = !atomic.compareAndSet(current, tuple._2)
            }
            b

          }
        }
      }
    }

    /** Написать эффект, который будет конкурентно обновлять счетчик
      */

    val atomicUpdate = for{
      ref <- Ref.make(0)
      _ <- UIO.foreachPar_((1 to 100))(_ => ref.update(_ + 1))
      res <- ref.get
    } yield res

    val print = atomicUpdate.flatMap(r => putStrLn(r.toString()))

    lazy val atomicUpdate2 = for{
      ref <- Ref.make(0)
      _ <- UIO.foreachPar_((1 to 100))(_ => ref.get.flatMap(c => ref.set(c + 1)))
      res <- ref.get
    } yield res

    val print2 = atomicUpdate2.flatMap(r => putStrLn(r.toString()))
  }

  object promises {
    val randomInt = nextInt.provideLayer(Random.live)

    val complete: UIO[(Int, Int)] = for{
      p <- Promise.make[Nothing, Int]
      _ <- p.complete(randomInt)
      l <- p.await
      r <- p.await
    } yield (l , r)



    lazy val completeWith: UIO[(Int, Int)] = for{
      p <- Promise.make[Nothing, Int]
      _ <- p.completeWith(randomInt)
      l <- p.await
      r <- p.await
    } yield (l , r)

  }

}
