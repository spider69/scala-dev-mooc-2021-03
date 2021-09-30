package module3

import zio.clock.Clock
import zio.duration.durationInt
import zio.macros.accessible
import zio.{Has, RIO, ULayer, ZIO, ZLayer}

import java.util.concurrent.TimeUnit
import scala.language.postfixOps

package object zio_homework {
  /**
   * 1.
   * Используя сервисы Random и Console, напишите консольную ZIO программу которая будет предлагать пользователю угадать число от 1 до 3
   * и печатать в когнсоль угадал или нет.
   */

  import zio.console._
  import zio.random._
  lazy val guessProgram = {
    lazy val readInt: RIO[Console, Int] = getStrLn.flatMap(str => ZIO.effect(str.toInt))
    lazy val readIntWithRetry: RIO[Console, Int] = readInt.orElse(
      putStrLn("Ошибка. Повторите ввод.") *> readIntWithRetry
    )

    def guessNumber(guessedNum: Int): RIO[Console, Unit] =
      readIntWithRetry
        .flatMap {
          case num if num == guessedNum =>
            putStrLn("Число угадано!")
          case _ =>
            putStrLn("Неверно. Введите число ещё раз.") *> guessNumber(guessedNum)
        }

    for {
      num <- nextIntBetween(1, 4)
      _ <- putStrLn("Угадайте число от 1 до 3! Введите число:")
      _ <- guessNumber(num)
    } yield ()
  }


  /**
   * 2. реализовать функцию doWhile, которая будет выполнять эффект до тех пор, пока его значение в условии не даст true
   */

  def doWhile[R, E, A](body: ZIO[R, E, A])(condition: A => Boolean): ZIO[R, E, A] =
    body.filterOrElse(condition)(_ => doWhile(body)(condition))

  /**
   * 3. Реализовать метод, который безопасно прочитает конфиг из файла, а в случае ошибки вернет дефолтный конфиг
   * и выведет его в консоль
   * Используйте эффект "load" из пакета config
   */

  def loadConfigOrDefault =
    config.load
      .orElse(
        ZIO.succeed(config.AppConfig("default appName", "default appUrl"))
          .tap(c => zio.console.putStrLn(c.toString))
      )

  /**
   * 4. Следуйте инструкциям ниже для написания 2-х ZIO программ,
   * обратите внимание на сигнатуры эффектов, которые будут у вас получаться,
   * на изменение этих сигнатур
   */


  /**
   *  4.1 Создайте эффект, который будет возвращать случайеым образом выбранное число от 0 до 10 спустя 1 секунду
   *  Используйте сервис zio Random
   */
  lazy val eff = zio.clock.sleep(1 second) *> zio.random.nextIntBetween(0, 11)

  /**
   * 4.2 Создайте коллукцию из 10 выше описанных эффектов (eff)
   */
   lazy val effects = List.fill(10)(eff)

  /**
   * 4.3 Напишите программу которая вычислит сумму элементов коллекци "effects",
   * напечатает ее в консоль и вернет результат, а также залогирует затраченное время на выполнение,
   * можно использовать ф-цию printEffectRunningTime, которую мы разработали на занятиях
   */

    lazy val app = RunningTimePrinter.printRunningTime(ZIO.reduceAll(ZIO.succeed(0), effects)(_ + _))

  /**
   * 4.4 Усовершенствуйте программу 4.3 так, чтобы минимизировать время ее выполнения
   */

    lazy val appSpeedUp = RunningTimePrinter.printRunningTime(ZIO.reduceAllPar(ZIO.succeed(0), effects)(_ + _))

  /**
   * 5. Оформите ф-цию printEffectRunningTime разработанную на занятиях в отдельный сервис, так чтобы ее
   * молжно было использовать аналогично zio.console.putStrLn например
   */

  @accessible
  object RunningTimePrinter {

    type RunningTimePrinter = Has[Service]

    trait Service {
      def printRunningTime[R, E, A](effect: ZIO[R, E, A]): ZIO[R with Console with Clock, E, A]
    }

    class ServiceImpl extends Service {

      override def printRunningTime[R, E, A](effect: ZIO[R, E, A]): ZIO[R with Console with Clock, E, A] = {
        for {
          startTime <- zio.clock.currentTime(TimeUnit.MILLISECONDS)
          result <- effect
          finishTime <- zio.clock.currentTime(TimeUnit.MILLISECONDS)
          totalTime = (finishTime - startTime) / 1000F
          _ <- zio.console.putStrLn(totalTime.toString + " seconds")
        } yield result
      }

    }

    lazy val live: ULayer[RunningTimePrinter] = ZLayer.succeed(new ServiceImpl)
  }
}
