package core

import akka.actor.Actor
import akka.event.Logging
import core.eventBus.SubchannelBusImpl.ControlMessages.{Disable, Enable}
import core.eventBus.SubchannelBusImpl.TopicType.Control

/**
  * Created by vp on 16. 9. 19.
  */
trait ActorBase extends Actor {
  val log = Logging(context.system, this)

  def idle: Receive = commonIdleBehavior orElse[Any, Unit] unrecognizedMessageHandler

  def active: Receive = commonActiveBehavior orElse[Any, Unit] unrecognizedMessageHandler

  import context._

  private def commonIdleBehavior: Receive = {
    case (Control, Enable) =>
      log.info("enable")
      become(active)
  }

  private def commonActiveBehavior: Receive = {
    case (Control, Disable) =>
      log.info("disable")
      unbecome()
  }

  private def unrecognizedMessageHandler: Receive = {
    case msg =>
      val errorMsg = s"Received unrecognized message: $msg"
      log.error(errorMsg)
      throw new RuntimeException(errorMsg)
  }
}
