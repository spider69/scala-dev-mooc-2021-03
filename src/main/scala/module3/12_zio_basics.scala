package module3

import zio.clock.{Clock, nanoTime}
import zio.console.{Console, getStrLn}
import zio.{IO, RIO, Task, UIO, URIO, ZIO}

import java.io.IOException
import scala.concurrent.Future
import scala.io.StdIn
import scala.util.Try



/** **
 * ZIO[-R, +E, +A] ----> R => Either[E, A]
 *
 */




object toyModel {

  /**
   * Используя executable encoding реализуем свой zio
   */

    case class ZIO[-R, +E, +A](run: R => Either[E, A]){ self =>

      def map[B](f: A => B): ZIO[R, E, B] = ZIO(r => self.run(r).map(f))

      def flatMap[R1 <: R, E1 >: E, B](f: A => ZIO[R1, E1,B]): ZIO[R1, E1, B] =
        ZIO(r => self.run(r).fold(ZIO.fail, f).run(r))
  }




  /**
   * Реализуем конструкторы под названием effect и fail
   */

   object ZIO{

     def effect[A](value: => A): ZIO[Any, Throwable, A] = {

       try{
         ZIO(_ => Right(value))
       } catch {
         case e => ZIO(_ => Left(e))
       }
     }

     def fail[E](e: E): ZIO[Any, E, Nothing] = ZIO(_ => Left(e))
   }


  /** *
   * Напишите консольное echo приложение с помощью нашего игрушечного ZIO
   */

   val echo: ZIO[Any, Throwable, Unit] = for{
     str <- ZIO.effect(StdIn.readLine())
     _ <- ZIO.effect(println(str))
   } yield ()

}

object zioTypeAliases {
  type Error
  type Environment

  // ZIO[-R, +E, +A]

  // ZIO[Any, Nothing, Nothing]


  lazy val _: Task[Int] = ??? // ZIO[Any, THROWABLE, Int]
  lazy val _: IO[Error, Int] = ??? // ZIO[Any, Error, Int]
  lazy val _: RIO[Environment, Int] = ??? // ZIO[Env, THROWABLE, Int]
  lazy val _: URIO[Environment, Int] = ??? // ZIO[Env, Nothing, Int]
  lazy val _: UIO[Int] = ??? // ZIO[Any, Nothing, Int]
}

object zioConstructors {


  // константа
  val _: UIO[Int] = ZIO.succeed(7)


  // любой эффект
  val _: Task[Unit] = ZIO.effect(println("Hello"))

  // любой не падающий эффект

  val _: UIO[Unit] = ZIO.effectTotal(println("hello"))




  // From Future
  val f: Future[Int] = ???
  val _: Task[Int] = ZIO.fromFuture(ec => f)


  // From try
  val t: Try[String] = ???
  val _: Task[String] = ZIO.fromTry(t)



  // From either
  val e: Either[String, Int] = ???
  val _: IO[String, Int] = ZIO.fromEither(e)



  // From option
  val opt : Option[Int] = ???
  val z: IO[Option[Nothing], Int] = ZIO.fromOption(opt)
  val zz: UIO[Option[Int]] = z.option
  val _: ZIO[Any, Option[Nothing], Int] = zz.some


  // From function
  val _: URIO[String, Unit] = ZIO.fromFunction[String, Unit](str => println(str))


  // особые версии конструкторов

  val _: UIO[Unit] = ZIO.unit

  val _: UIO[Option[Nothing]] = ZIO.none

  val _: UIO[Nothing] = ZIO.never // while(true)

  val _: ZIO[Any, Nothing, Nothing] = ZIO.die(new Throwable("Died"))

  val _: ZIO[Any, Int, Nothing] = ZIO.fail(7)

}



object zioOperators {

  /** *
   *
   * 1. Создать ZIO эффект который будет читать строку из консоли
   */

  lazy val readLine: Task[String] = ZIO.effect(StdIn.readLine())

  /** *
   *
   * 2. Создать ZIO эффект который будет писать строку в консоль
   */

  def writeLine(str: String): Task[Unit] = ZIO.effect(println(str))

  /** *
   * 3. Создать ZIO эффект котрый будет трансформировать эффект содержащий строку в эффект содержащий Int
   */

  lazy val lineToInt: ZIO[Any, Throwable, Int] = readLine.flatMap(str => ZIO.effect(str.toInt))

  /** *
   * 3.Создать ZIO эффект, который будет работать как echo для консоли
   *
   */

  lazy val echo = ???

  /**
   * Создать ZIO эффект, который будет привествовать пользователя и говорить, что он работает как echo
   */

  lazy val greetAndEcho: ZIO[Any, Throwable, (Unit, Unit)] = ???

  /**
   * Дпугие версии ZIP
   */

  lazy val a1: Task[Int] = ???
  lazy val b1: Task[String] = ???


  val _: ZIO[Any, Throwable, Int] = ZIO.effect(println("Hello")) *> ZIO.effect(1 + 1)



  val _: ZIO[Any, Throwable, (Int, String)] = a1.zip(b1)

  // zipRight
  lazy val _: ZIO[Any, Throwable, String] = a1 *> b1

  // zipLeft
  lazy val _: ZIO[Any, Throwable, Int] = a1 <* b1


  // greet and echo улучшенный
  lazy val _: ZIO[Any, Throwable, Unit] = ???
  lazy val _: ZIO[Any, Throwable, Unit] = ???


  /**
   * Используя уже созданные эффекты, написать программу, которая будет считывать поочереди считывать две
   * строки из консоли, преобразовывать их в числа, а затем складывать их
   */

  val r1: ZIO[Any, Throwable, Int] = for {
    n1 <- lineToInt
    n2 <- lineToInt
  } yield n1 + n2

  /**
   * Второй вариант
   */

  val r2: ZIO[Any, Throwable, Int] = ???

  /**
   * Доработать написанную программу, чтобы она еще печатала результат вычисления в консоль
   */

  lazy val r3 = ???


  lazy val a: Task[Int] = ???
  lazy val b: Task[String] = ???

  /**
   * последовательная комбинация эффектов a и b
   */
  lazy val ab1: ZIO[Any, Throwable, (Int, String)] = ??? // zip

  /**
   * последовательная комбинация эффектов a и b
   */
  lazy val ab2: ZIO[Any, Throwable, Int] = ??? // <*

  /**
   * последовательная комбинация эффектов a и b
   */
  lazy val ab3: ZIO[Any, Throwable, String] = ??? // *>


  /**
   * Последовательная комбинация эффета b и b, при этом результатом должна быть конкатенация
   * возвращаемых значений
   */
  lazy val ab4: ZIO[Any, Throwable, String] = b.zipWith(b)(_ + _) // zipWith

  lazy val c: ZIO[Clock, Throwable, Int] = ZIO.effect("").as(7) // as


  def readFile(fileName: String): ZIO[Any, IOException, String] = ???

  lazy val _: URIO[Any, String] = readFile("test.txt").orDie

}


object zioRecursion {

  /** **
   * Написать программу, которая считывает из консоли Int введнный пользователем,
   * а в случае ошибки, сообщает о некорректном вводе, и просит ввести заново
   *
   */



  lazy val readInt: ZIO[Console, Throwable, Int] =
    ZIO.effect(StdIn.readLine()).flatMap(str => ZIO.effect(str.toInt))


  lazy val readIntOrRetry: ZIO[Console, Throwable, Int] = readInt.orElse(
    ZIO.effect(println("Ошибка, повторите ввод")) *> readIntOrRetry
  )

  /**
   * Считаем факториал
   */
  def factorial(n: Int): Int = {
    if(n <= 1) n
    else n * factorial(n - 1)
  }

  /**
   * Написать ZIO версию ф-ции факториала
   *
   */
  def factorialZ(n: BigDecimal): Task[BigDecimal] = {
    if( n<= 1) ZIO.succeed(n)
    else ZIO.succeed(n).zipWith(factorialZ(n -1))(_ * _)
  }

}