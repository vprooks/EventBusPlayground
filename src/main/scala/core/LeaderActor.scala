package core

import java.io.File

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import core.eventBus.SubchannelBusImpl

class LeaderActor extends Actor {

  override def preStart(): Unit = {
    val bus = new SubchannelBusImpl // Create a bus

    // Create actors
    val camera1 = context.actorOf(CameraActor.props("/camera1/image", "/camera1/control", bus), name = "camera1")
    val writer1 = context.actorOf(WriterActor.props("/camera1/image", "/writer/control", bus), name = "writer1")
    val vis1 = context.actorOf(VisActor.props("/camera1/image", "/vis/control", bus), name = "vis1")
    val camera2 = context.actorOf(CameraActor.props("/camera2/image", "/camera2/control", bus), name = "camera2")
    val writer2 = context.actorOf(WriterActor.props("/camera2/image", "/writer/control", bus), name = "writer2")
    val vis2 = context.actorOf(VisActor.props("/camera2/image", "/vis/control", bus), name = "vis2")
    // Send "enable" commands to all the actors
    // Note that you can send directly to each of the actors
    bus.publish(("/camera1/control", "enable"))
    bus.publish(("/camera2/control", "enable"))
    bus.publish(("/writer/control", "enable"))
    bus.publish(("/vis/control", "enable"))
  }

  override def receive: Receive = {
    case _ =>
  }
}

object LeaderActor {
  def main(args: Array[String]) = {
    val configFile = getClass.getClassLoader.getResource("leader.conf").getFile
    val config = ConfigFactory.parseFile(new File(configFile))
    val system = ActorSystem("LeaderSystem", config)
    val leader = system.actorOf(Props[LeaderActor], name = "leader")
  }
}

