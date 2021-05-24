package module1


import module2.implicits.{implicit_conversions, implicit_scopes}
import module2.{higher_kinded_types, implicits, type_classes}
import module3.functional_effects.functionalProgram.{declarativeEncoding, executableEncoding}
import module3.{zioOperators, zioRecursion}
import zio.ZIO

import scala.language.{existentials, implicitConversions, postfixOps}

object App {

  def main(args: Array[String]): Unit = {
    zio.Runtime.default.unsafeRun(zioRecursion.readIntOrRetry)
  }
}

