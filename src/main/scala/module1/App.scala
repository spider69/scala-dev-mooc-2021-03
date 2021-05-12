package module1


import module2.implicits.{implicit_conversions, implicit_scopes}
import module2.{higher_kinded_types, implicits, type_classes}

import scala.language.{existentials, implicitConversions, postfixOps}

object App {

  def main(args: Array[String]): Unit = {
//    higher_kinded_types
//    type_classes
 //   implicit_conversions
    implicit_scopes
  }
}

