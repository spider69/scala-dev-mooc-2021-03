package module1

import module1.subtyping.adt.sealed_traits.Card.{Clubs, Diamonds, Spades}


object subtyping {


  /**
   * Type Operators
   */

  trait Vehicle
  trait Car        extends Vehicle
  trait Moto       extends Vehicle
  object Harley    extends Moto
  object Mustang   extends Car

  type IsSubtypeOf[A, B >: A]

  type IsSupertypeOf[A, B <: A]


  /**
   *
   * С помощью типа IsSubtypeOf выразить отношение Car и Vehicle
   *
   */

//  lazy val t1 : IsSubtypeOf[Vehicle, Car] = ???


  /**
   *
   * С помощью типа IsSubtypeOf выразить отношение Car и Mustang
   *
   */

//  lazy val t2 : IsSubtypeOf[Mustang.type, Car] = ???


  /**
   *
   * С помощью типа выразить отношение Vehicle и Harley, причем чтобы они шли в этом порядке
   *
   */

//  lazy val t3: IsSupertypeOf[Vehicle, Harley.type ] = ???


  /**
   * В этом примере вам нужно правильно выбрать оператор отношения,
   * чтобы среди идущих ниже выражений, те которые корректны по смыслу компилировались, а остальные нет
   */

  def isInstanceOf[A, B >: A](a: A): Unit = ???




/*  lazy val mustCompile1    = isInstanceOf[Mustang.type, Car](Mustang)
  lazy val mustCompile2    = isInstanceOf[Harley.type, Moto](Harley)
  lazy val mustNotCompile1 = isInstanceOf[Mustang.type, Moto](Mustang)
  lazy val mustNotCompile2 = isInstanceOf[Harley.type, Car](Harley)*/


  // Вариантность
  class Garage[T]


  // Вариантность полей
  // val
//  class Garage2[-T](val vehicle: T)

  //  val hondaShadow = new Moto {}
  //  val shadowGarage = new Garage2[Vehicle](hondaShadow)
  //  val carGarage: Garage2[Car] = shadowGarage
  //  val _: Car = carGarage.vehicle


  // var
//  class Garage3[+T](var vehicle: T)

/*  val hondaShadow = new Moto {}
  val shadowGarage = new Garage3[Vehicle](hondaShadow)

  val _ = shadowGarage.vehicle = new Car {}*/


  // method args
/*  class Garage4[+T]{
    def put(vehicle: T): Unit = ???
  }*/

  //  val garageVehicle: Garage4[Vehicle] = new Garage4[Car]
  //  val _ = garageVehicle.put(new Moto {})



  // method return value
  abstract class Garage5[+T]{
    def get: T
  }

  //  val garageVehicle = new Garage5[Vehicle] {
  //    override def get: Vehicle = new Moto {}
  //  }
  //
  //  val carGarage: Garage5[Car] = garageVehicle
  //
  //  val _ = carGarage.get

  abstract class Cleaner[-T]{
    def clean(t: T)
  }

  val cleaner: Cleaner[Vehicle] = new Cleaner[Vehicle] {
    override def clean(t: Vehicle): Unit = println("Cleaned")
  }

  def cleaner[T](t: T, cleaner: Cleaner[T]) = cleaner.clean(t)

  cleaner[Car](new Car {}, cleaner)

  val l: List[Vehicle] = List(new Car {}, new Moto {})




  trait Box[+T] {
    def get: T
  }


  // val a : IsSubtypeOf = ???


  trait Consumer[+T] {
    def consume[TT >: T](v: TT): Unit
    def produce: T
  }

  //  val b : IsSupertypeOf






  object adt{


    object tuples{


      // Boolean * Unit
      // true unit
      // false unit

      /**
       * Tuples
       *
       */
      type ProductUnitBoolean = (Unit, Boolean)

      val c: ProductUnitBoolean = ((), true)
      val c2: ProductUnitBoolean = ((), false)


      /**
       * Реализовать тип Person который будет содержать имя и возраст
       */

      //      type Person = (String, Int)

      //      val person: Person = ("John", 21)


      /**
       *
       *  Реализовать тип `CreditCard` который может содержать номер (String),
       *  дату окончания (java.time.YearMonth), имя (String), код безопастности (Short)
       */
      //      type CreditCard = (String, java.time.YearMonth, String, Short)


    }

    object case_classes {
      /**
       * Case classes
       */



      //  Реализовать Person с помощью case класса
      class Person(val name: String, val age: Int)



      // Создать экземпляр для Tony Stark 42 года
      lazy val tonyStark = new Person("Tony", 42)


      // Создать case class для кредитной карты




      // Pattern matching


      /**
       * используя паттерн матчинг напечатать имя и возраст
       */

      object Person{
        def unapply(p: Person): Some[(String, Int)] = Some((p.name, p.age))
      }

      val Person(name, age) = tonyStark
      val person = new Person("", 4)


      def printNameAndAge: Unit = tonyStark match {
        case Person(name, age) => println(s"$name $age")
      }






      final case class Employee(name: String, address: Address)
      final case class Address(street: String, number: Int)

      val alex = Employee("Alex", Address("XXX", 221))

      /**
       * воспользовавшись паттерн матчингом напечатать номер из поля адрес
       */

      alex match {
        case Employee(_, Address(_, number)) => println(number)
      }



      /**
       * Паттерн матчинг может содержать литералы.
       * Реализовать паттерн матчинг на alex с двумя кейсами.
       * 1. Имя должно соотвествовать Alex
       * 2. Все остальные
       */

      alex match {
        case Employee("Alex", _) => println("alex")
        case _ => println("no alex")
      }


      /**
       * Паттерны могут содержать условия. В этом случае case сработает,
       * если и паттерн совпал и условие true.
       * Условия в паттерн матчинге называются гардами.
       */

      alex match {
        case Employee(name, _) if name == "Alex" => println("alex")
        case _ => println("no alex")
      }


      /**
       * Реализовать паттерн матчинг на alex с двумя кейсами.
       * 1. Имя должно начинаться с A
       * 2. Все остальные
       */


      /**
       *
       * Мы можем поместить кусок паттерна в переменную использую `as` паттерн,
       * x @ ..., где x это любая переменная. Это переменная может использоваться, как в условии,
       * так и внутри кейса
       */

      alex match {
        case e @ Employee(name, _)  => println(e.address.street)
        case _ => println("no alex")
      }


      /**
       * Мы можем использовать вертикальную черту `|` для матчинга на альтернативы
       */

      sealed trait A
      case class B(v: Int) extends A
      case class C(v: Int) extends A
      case class D(v: Int) extends A

      val a: A = ???


      val str: String = ???

      str match {
        case alex =>
        case _ =>
      }

      alex match {
        case e @ Employee(alex, _)  => println(e.address.street)
        case _ => println("no alex")
      }


    }


    object either{


      /**
       * Sum
       */

      // Unit + Boolean  () | true | false




      /**
       * Either - это наиболее общий способ хранить один из двух или более кусочков информации в одно время.
       * Также как и кортежи обладает целым рядом полезных методов
       * Иммутабелен
       */

      type IntOrString = Either[Int, String]

      /**
       * Реализовать экземпляр типа IntOrString с помощью конструктора Right(_)
       */
      val intOrString: IntOrString = Right("Hello")


      /**\
       * Реализовать тип PaymentMethod который может быть представлен одной из альтернатив
       */
      type PaymentMethod = Either[CreditCard, Either[WireTransfer, Cash]]

      final case class CreditCard()
      final case class WireTransfer()
      final case class Cash()

    }

    object sealed_traits{
      /**
       * Также Sum type можно представить в виде sealed trait с набором альтернатив
       */


      sealed trait Card
      object Card {
        final case class Clubs(points: Int)    extends Card // крести
        final case class Diamonds(points: Int) extends Card // бубны
        final case class Spades(points: Int)   extends Card // пики
        final case class Hearts(points: Int)   extends Card // червы
      }

      lazy val card: Card = Card.Spades(10)


      /**
       * Написать паттерн матчинг на 10 пику, и на все остальное
       */


      /**
       * Написать паттерн матчинг который матчит карты номиналом >= 10
       */

    }

  }
}