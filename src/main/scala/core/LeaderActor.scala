package core

import java.io.File

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import core.eventBus.SubchannelBusImpl
import core.eventBus.SubchannelBusImpl.ControlMessages.Enable
import core.eventBus.SubchannelBusImpl.TopicType.{Control, Image}

class LeaderActor extends Actor {

  override def preStart(): Unit = {
    val bus = new SubchannelBusImpl // Create a bus

    // Create actors
    val camera1 = context.actorOf(
      CameraActor.props(
        Map(Image -> "/camera1/image",
          Control -> "/camera1/control"), bus),
      name = "camera1")
    val writer1 = context.actorOf(
      WriterActor.props(
        Map(Image -> "/camera1/image",
          Control -> "/writer/control"), bus),
      name = "writer1")
    val vis1 = context.actorOf(
      VisActor.props(
        Map(Image -> "/camera1/image",
          Control -> "/vis/control"), bus),
      name = "vis1")
    val camera2 = context.actorOf(
      CameraActor.props(
        Map(Image -> "/camera2/image",
          Control -> "/camera2/control"), bus),
      name = "camera2")
    val writer2 = context.actorOf(
      WriterActor.props(
        Map(Image -> "/camera2/image",
          Control -> "/writer/control"), bus),
      name = "writer2")
    val vis2 = context.actorOf(
      VisActor.props(
        Map(Image -> "/camera2/image",
          Control -> "/vis/control"), bus),
      name = "vis2")
    // Send "enable" commands to all the actors
    // Note that you can send directly to each of the actors
    bus.publish((Control, "/camera1/control", Enable))
    bus.publish((Control, "/camera2/control", Enable))
    bus.publish((Control, "/writer/control", Enable))
    bus.publish((Control, "/vis/control", Enable))
  }

  override def receive: Receive = Actor.emptyBehavior
}

object LeaderActor {
  def main(args: Array[String]) = {
    val configFile = getClass.getClassLoader.getResource("leader.conf").getFile
    val config = ConfigFactory.parseFile(new File(configFile))
    val system = ActorSystem("LeaderSystem", config)
    val leader = system.actorOf(Props[LeaderActor], name = "leader")
  }
}

