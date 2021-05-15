import module1.opt.Option

val a = Option.Some(1)
val b = Option.Some(2)
val none: Option[Int] = Option.None

a.isEmpty
b.isEmpty
none.isEmpty

a.get
b.get
try { none.get } catch { case e: Throwable => e.getMessage }

a.getOrElse(199)
b.getOrElse(199)
none.getOrElse(199)

a.map(_ * 2)
b.map(_ * 2)
none.map(_ * 2)

a.flatMap(v => Option.Some(v * 100))
b.flatMap(v => Option.Some(v * 100))
none.flatMap(v => Option.Some(v * 100))

a.printIfAny
b.printIfAny
none.printIfAny

a.orElse(Option.Some(300))
b.orElse(Option.None)
none.orElse(Option.Some(500))

a.zip(b)
b.zip(none)
none.zip(a)

a.filter(_ % 2 == 0)
b.filter(_ % 2 == 0)
none.filter(_ % 2 == 0)
