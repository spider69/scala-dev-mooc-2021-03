import Dependencies.versions.ZioVersion
import Dependencies.versions.PureconfigVersion
import sbt.ModuleID
import sbt._

object Dependencies {

  object versions {
    lazy val KindProjectorVersion = "0.10.3"
    lazy val ZioVersion = "1.0.4"
    lazy val PureconfigVersion = "0.12.3"
  }


  lazy val zio: Seq[ModuleID] = Seq(
    "dev.zio" %% "zio" % ZioVersion,
    "dev.zio" %% "zio-interop-cats" % "2.2.0.1",
    "dev.zio" %% "zio-logging-slf4j" % "0.5.6",
    "dev.zio" %% "zio-test" % ZioVersion,
    "dev.zio" %% "zio-test-sbt" % ZioVersion,
    "dev.zio" %% "zio-macros" % ZioVersion
  )

  lazy val pureconfig: Seq[ModuleID] = Seq(
    "com.github.pureconfig" %% "pureconfig" % PureconfigVersion,
    "com.github.pureconfig" %% "pureconfig-cats-effect" % PureconfigVersion
  )
}
