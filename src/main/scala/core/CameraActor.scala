package core

import akka.actor.{Cancellable, Props}
import core.eventBus.SubchannelBusImpl
import core.eventBus.SubchannelBusImpl.ControlMessages._
import core.eventBus.SubchannelBusImpl.TopicType
import core.eventBus.SubchannelBusImpl.TopicType._

import scala.concurrent.duration._

class CameraActor(topics: Map[TopicType, String], bus: SubchannelBusImpl) extends ActorBase {
  var scheduler: Cancellable = _

  def receive = idle

  import context._

  override def preStart() = {
    // Subscribe to control topic only
    log.info(s"subscribe to ${topics(Control)}")
    bus.subscribe(self, (Control, topics(Control)))
  }

  private def idleBehavior: Receive = {
    case (Control, Enable) =>
      log.info(s"enable @ ${topics(Control)}")
      system.scheduler.schedule(
        1 milliseconds,
        33 milliseconds,
        self,
        "send"
      )
      become(active)
  }

  private def activeBehavior: Receive = {
    case "send" =>
      log.info(s"publish image to ${topics(Image)}")
      bus.publish((Image, topics(Image), "image"))
  }

  override def idle: Receive = idleBehavior orElse[Any, Unit] super.idle

  override def active: Receive = activeBehavior orElse[Any, Unit] super.active
}

object CameraActor {
  def props(topics: Map[TopicType, String], bus: SubchannelBusImpl) =
    Props(new CameraActor(topics, bus))
}
