package core

import akka.actor.Props
import core.eventBus.SubchannelBusImpl
import core.eventBus.SubchannelBusImpl.TopicType
import core.eventBus.SubchannelBusImpl.TopicType.Image

class VisActor(topics: Map[TopicType, String], bus: SubchannelBusImpl) extends ActorBase {
  def receive = idle

  override def preStart() = {
    topics map (topic => {
      log.info(s"subscribe to $topic")
      bus.subscribe(self, topic)
    })
  }

  private def activeBehavior: Receive = {
    case (Image, "image") =>
      log.info(s"vis image from ${topics(Image)}")
  }

  override def active: Receive = activeBehavior orElse[Any, Unit] super.active
}

object VisActor {
  def props(topics: Map[TopicType, String], bus: SubchannelBusImpl) =
    Props(new VisActor(topics, bus))
}
