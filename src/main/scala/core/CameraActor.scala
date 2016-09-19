package core

import akka.actor.{Actor, Cancellable, Props}
import akka.event.Logging
import core.eventBus.SubchannelBusImpl

import scala.concurrent.duration._

class CameraActor(outputTopic: String, controlTopic: String, bus: SubchannelBusImpl) extends Actor {
  val log = Logging(context.system, this)

  override def preStart(): Unit = {
    log.debug(s"$controlTopic $outputTopic")
    bus.subscribe(self, controlTopic)
  }

  var scheduler: Cancellable = _

  def receive = idle

  import context._

  @scala.throws[Exception](classOf[Exception])
  def idle: Receive = {
    case "enable" =>
      log.info(s"enable @ $controlTopic")
      become(active)
      system.scheduler.schedule(
        1 milliseconds,
        33 milliseconds,
        self,
        "send"
      )
    case _ =>
      val msg = "received unrecognized message"
      log.error(msg)
      throw new RuntimeException(msg)
  }

  def active: Receive = {
    case "send" =>
      // TODO: start sending messages, using scheduler maybe
      log.info(s"publish image to $outputTopic")
      bus.publish((outputTopic, "image"))
    case "disable" =>
      unbecome()
      log.info("disable")
      scheduler.cancel()
    case _ =>
      val msg = "received unrecognized message"
      log.error(msg)
      throw new RuntimeException(msg)
  }
}

object CameraActor {
  def props(outputTopic: String, controlTopic: String, bus: SubchannelBusImpl) =
    Props(new CameraActor(outputTopic, controlTopic, bus))
}
