package module1


import module2.implicits.{implicit_conversions, implicit_scopes}
import module2.{higher_kinded_types, implicits, type_classes}
import module3.functional_effects.functionalProgram.{declarativeEncoding, executableEncoding}
import module3.{zioOperators, zioRecursion}
import zio.Cause.{Both, Fail, Internal}
import zio.console.{Console, putStrLn}
import zio.{IO, UIO, ZIO}

import scala.concurrent.Future
import scala.language.{existentials, implicitConversions, postfixOps}

object App {

  def main(args: Array[String]): Unit = {

    sealed trait NotificationError
    case object NotificationByEmailFailed extends NotificationError
    case object NotificationBySMSFailed extends NotificationError

    val z1 = ZIO.fail(NotificationByEmailFailed)
    val z2 = ZIO.fail(NotificationBySMSFailed)

    val app = z1.zipPar(z2).tapCause {
      case Both(c1, c2) =>
        putStrLn(c1.toString) *> putStrLn(c2.toString)
    }.orElse(putStrLn("app is failed"))

    zio.Runtime.default.unsafeRun(app)
  }
}

