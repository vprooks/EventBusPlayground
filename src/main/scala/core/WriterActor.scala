package core

import akka.actor.{Actor, Props}
import akka.event.Logging
import core.eventBus.SubchannelBusImpl

class WriterActor(inputTopic: String, controlTopic: String, bus: SubchannelBusImpl) extends Actor {
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
      log.info("active")
  }

  def active: Receive = {
    case "image" =>
      log.info("writing image")
    case "disable" =>
      log.info("disable")
      unbecome()
  }
}

object WriterActor {
  def props(inputTopic: String, controlTopic: String, bus: SubchannelBusImpl) =
    Props(new WriterActor(inputTopic, controlTopic, bus))
}
