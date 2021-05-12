package module2

import module2.type_classes.JsValue.{JsNull, JsNumber, JsString}

object type_classes{


  sealed trait JsValue
  object JsValue{
    final case class JsObject(get: Map[String, JsValue]) extends JsValue
    final case class JsString(get: String) extends JsValue
    final case class JsNumber(get: Double) extends JsValue
    final case object JsNull extends JsValue
  }

  trait JsonWriter[T]{
    def write(v: T): JsValue
  }

  object JsonWriter{
    def apply[T](implicit jsonWriter: JsonWriter[T]): JsonWriter[T] = jsonWriter

    implicit val str: JsonWriter[String] = new JsonWriter[String] {
      def write(v: String): JsValue = JsString(v)
    }

    implicit val int: JsonWriter[Int] = new JsonWriter[Int] {
      def write(v: Int): JsValue = JsNumber(v)
    }

    def createInstance[T](f: T => JsValue): JsonWriter[T] = new JsonWriter[T] {
      override def write(v: T): JsValue = f(v)
    }

    implicit def opt[A](implicit jsonWriter: JsonWriter[A]) =
      createInstance[Option[A]]{
        case Some(v) => jsonWriter.write(v)
        case None => JsNull
      }
  }
  
  object JsonSyntax{
    implicit class jsonOps[A](val v: A) extends AnyVal {
      def toJson(implicit ev: JsonWriter[A]): JsValue = ev.write(v)
    }
  }

  import JsonSyntax._

  "foo".toJson
  1.toJson

  trait A
  val a: A = ???


  Option(1).toJson
  Option("aaa").toJson





  // Bindable

  trait Bindable[F[_]]{
    def map[A, B](el: F[A])(f: A => B): F[B]
    def flatMap[A, B](el: F[A])(f: A => F[B]): F[B]
  }

  def tupleF[F[_]: Bindable, A, B](fa: F[A], fb: F[B]): F[(A, B)] =
    {
      Bindable[F].flatMap(fa)(a => Bindable[F].map(fb)(b => (a, b)))
    }

  object Bindable{

    def apply[F[_]](implicit ev: Bindable[F]): Bindable[F] = ev

    implicit val opt = new Bindable[Option] {
      override def map[A, B](el: Option[A])(f: A => B): Option[B] =
        el.map(f)

      override def flatMap[A, B](el: Option[A])(f: A => Option[B]): Option[B] =
        el.flatMap(f)
    }

    implicit val list: Bindable[List] = ???
  }


  val optA: Option[Int] = Some(1)
  val optB: Option[Int] = Some(2)

  val list1 = List(1, 2, 3)
  val list2 = List(4, 5, 6)

  println(tupleF(optA, optB))
  println(tupleF(list1, list2))


  // Context Bounds, Ordering example


}