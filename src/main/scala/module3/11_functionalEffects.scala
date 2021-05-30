package module3

import scala.io.StdIn

object functional_effects {


  object simpleProgram {

    val greet = {
      println("Как тебя зовут?")
      val name = StdIn.readLine()
      println(s"Привет, $name")
    }

    val askForAge = {
      println("Сколько тебе лет?")
      val age = StdIn.readInt()
      if (age > 18) println("Можешь проходить")
      else println("Ты еще не можешь пройти")
    }


    def greetAndAskForAge = {
      greet
      askForAge
    }


  }


  object functionalProgram {

    /**
     * Executable encoding and Declarative encoding
     */

    object executableEncoding {

      /**
       * 1. Объявить исполняемую модель Console
       */

       case class Console[A](unsafeRun: () => A){ self =>

        def map[B](f: A => B): Console[B] = {
          flatMap(f.andThen(b => Console.console(b)))
        }

        def flatMap[B](f: A => Console[B]): Console[B] =
          Console.console(f(self.unsafeRun()).unsafeRun())

        def cond[B](predicate: A => Boolean)(success: Console[B])(failure: Console[B]) = ???

      }


      /**
       * 2. Объявить конструкторы
       */

      object Console{

        def console[A](a: => A): Console[A] = Console(() => a)
        def printLine(str: String): Console[Unit] = Console(() => println(str))
        def readLine(): Console[String] = Console(() => StdIn.readLine())
      }


      /**
       * 3. Описать желаемую программу с помощью нашей модель
       */

      lazy val p: Console[Unit] = for{
        _ <- Console.printLine("Как тебя зовут?")
        name <- Console.readLine
        _ <- Console.printLine(s"Привет, $name")
      } yield ()


      /**
       * 4. Написать операторы
       */

    }


    object declarativeEncoding {

      /**
       * 1. Объявить декларативную модель Console
       */

      sealed trait Console[+A]
      case class Println[A](string: String, rest: Console[A]) extends Console[A]
      case class ReadLine[A](str: String => Console[A]) extends Console[A]
      case class Return[A](value: () => A) extends Console[A]


      val p1 = Println("Как тебя зовут?",
        ReadLine(str =>
          Println(s"Привет, $str",
            Return(() => ())))
      )

      /**
       * 2. Написать конструкторы
       */


       object Console{
         def succeed[A](v: => A): Console[A] = Return(() => v)
         def printLine(str: String): Console[Unit] = Println(str, succeed(()))
         def readLine: Console[String] = ReadLine(str => succeed(str))
      }


      /**
       * 3. Описать желаемую программу с помощью нашей модели
       */


      /**
       * 4. Написать операторы
       *
       */

      object consoleOps{
        implicit class ops[A](self: Console[A]){

          def map[B](f: A => B): Console[B] = flatMap(v => Console.succeed(f(v)))

          def flatMap[B](f: A => Console[B]): Console[B] = self match {
            case Println(string, rest) => Println(string, rest.flatMap(f))
            case ReadLine(ff) => ReadLine(str => ff(str).flatMap(f))
            case Return(value) => f(value())
          }
        }
      }

      import consoleOps._


      val p2: Console[Unit] = for{
        _ <- Console.printLine("Как тебя зовут?")
        name <- Console.readLine
        _ <- Console.printLine(s"Привет, $name")
      } yield ()

      val p3 = for{
        _ <- Console.printLine("Сколько тебе лет?")
        age <- Console.readLine
        _ <- if(age.toInt >= 18) Console.printLine(s"Ты можешь пройти") else Console.printLine(s"Тебе не пройти")
      } yield ()

      val p4 = for{
        _ <- p2
        _ <- p3
      } yield ()





      /**
       * 5. Написать интерпретатор для нашей ф-циональной модели
       *
       */

      def interpret[A](console: Console[A]): A = console match {
        case Println(str, rest) =>
          println(str)
          interpret(rest)
        case ReadLine(f) =>
          interpret(f(StdIn.readLine()))
        case Return(v) => v()
      }

      def describe[A](console: Console[A]): Unit = ???




      /**
       * Реализуем туже прошрамму что и в случае с исполняемым подходом
       */

    }

  }

}