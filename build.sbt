import sbt.Keys.libraryDependencies
import sbt.*

val scala3Version = "3.3.1"
val scalaFXVersion = "16.0.0-R24"

ThisBuild / libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.19"
ThisBuild / libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % "test"
ThisBuild / libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.5.3"
ThisBuild / libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.8.8"


lazy val rest = project.in(file("rest"))
  .settings(
    name := "rest",
    Compile / mainClass := Some("rest.RestServer"),
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % "10.5.3",
      "com.typesafe.akka" %% "akka-stream" % "2.8.8"
    )
  )
  .dependsOn(controller, model, util, aview, root) // möglicherweise falsche Dependencies!

lazy val model = project.in(file("model"))
  .settings(
    name := "model",
    Compile / mainClass := Some("model.ModelServer"),
    scalaVersion := scala3Version,
    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.10.3",
    libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.2.0",
    libraryDependencies += "com.typesafe.slick" %% "slick-hikaricp" % "3.5.1",
    libraryDependencies += "com.typesafe.slick" %% "slick" % "3.5.1",
    libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.6.4",
    libraryDependencies += "org.postgresql" % "postgresql" % "42.7.3",
    libraryDependencies += ("org.mongodb.scala" %% "mongo-scala-driver" % "4.3.3").cross(CrossVersion.for3Use2_13)
  )

lazy val util = project.in(file("util"))
  .settings(scalaVersion := scala3Version)

lazy val controller = project.in(file("controller"))
  .settings(
    name := "controller",
    Compile / mainClass := Some("controller.ControllerServer"),
    scalaVersion := scala3Version
  )
  .dependsOn(model, util)

lazy val aview = project.in(file("aview"))
  .settings(
    name := "aview",
    Compile / mainClass := Some("aview.UIServer"),
    scalaVersion := scala3Version,
    libraryDependencies += ("org.scala-lang.modules" %% "scala-swing" % "3.0.0").cross(CrossVersion.for3Use2_13)
  )
  .dependsOn(controller, util)

lazy val main = project.in(file("main"))
  .settings(
    name := "main",
    Compile / mainClass := Some("main.wordle"),
    scalaVersion := scala3Version,
    libraryDependencies += ("org.scala-lang.modules" %% "scala-swing" % "3.0.0").cross(CrossVersion.for3Use2_13)
  )
  .dependsOn(aview, controller, util, model)


lazy val root = project
  .in(file("."))
  .settings(
    name := "wordle1",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,
    libraryDependencies +="org.scalafx" %% "scalafx" % scalaFXVersion,
      libraryDependencies ++= {
          // Determine OS version of JavaFX binaries
          lazy val osName = System.getProperty("os.name") match {
              case n if n.startsWith("Linux") => "linux"
              case n if n.startsWith("Mac") => "mac"
              case n if n.startsWith("Windows") => "win"
              case _ => throw new Exception("Unknown platform!")
          }
          Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
            .map(m => "org.openjfx" % s"javafx-$m" % "16" classifier osName)
      },
      libraryDependencies += ("org.scala-lang.modules" %% "scala-swing" % "3.0.0").cross(CrossVersion.for3Use2_13),
    libraryDependencies += "net.codingwell" %% "scala-guice" % "7.0.0",
    libraryDependencies += "com.google.inject" % "guice" % "7.0.0",
    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.10.3",
      libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.2.0"
  )
  .dependsOn(aview, controller, util, model)
  .aggregate(util, aview, controller, model)