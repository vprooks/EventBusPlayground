package core

import akka.actor.{Actor, Props}
import akka.event.Logging
import core.eventBus.SubchannelBusImpl

class VisActor(inputTopic: String, controlTopic: String, bus: SubchannelBusImpl) extends Actor {
  val log = Logging(context.system, this)

  override def preStart(): Unit = {
    bus.subscribe(self, inputTopic)
    bus.subscribe(self, controlTopic)
  }

  def receive = idle

  import context._

  def idle: Receive = {
    case "enable" =>
      become(active)
      log.info("become active")
  }

  def active: Receive = {
    case "image" =>
      log.info("show image")
    case "disable" =>
      log.info("disable")
      unbecome()
  }
}

object VisActor {
  def props(inputTopic: String, controlTopic: String, bus: SubchannelBusImpl) =
    Props(new VisActor(inputTopic, controlTopic, bus))
}
