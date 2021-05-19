package module1


import module2.implicits.{implicit_conversions, implicit_scopes}
import module2.{higher_kinded_types, implicits, type_classes}
import module3.functional_effects.functionalProgram.{declarativeEncoding, executableEncoding}

import scala.language.{existentials, implicitConversions, postfixOps}

object App {

  def main(args: Array[String]): Unit = {
     //declarativeEncoding.interpret(declarativeEncoding.p2)
     executableEncoding.p.unsafeRun()
  }
}

