name := "EventBusPlayground"

version := "1.0"

scalaVersion := "2.11.8"

cancelable in Global := true

scalacOptions ++= Seq("-feature", "-language:postfixOps")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.10"
)
