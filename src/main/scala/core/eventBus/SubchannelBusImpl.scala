package core.eventBus

import akka.actor.ActorRef
import akka.event.{EventBus, SubchannelClassification}
import akka.util.Subclassification
import core.eventBus.SubchannelBusImpl.TopicType

class SubchannelBusImpl extends EventBus with SubchannelClassification {
  type Event = (TopicType, String, Any)
  type Classifier = (TopicType, String)
  type Subscriber = ActorRef

  override protected def classify(event: Event): Classifier =
    (event._1, event._2)

  protected def subclassification = new Subclassification[Classifier] {
    def isEqual(x: Classifier, y: Classifier) = x == y

    def isSubclass(x: Classifier, y: Classifier) =
      x._2.startsWith(y._2) && x._1 == y._1
  }

  override protected def publish(event: Event, subscriber: Subscriber): Unit =
    subscriber ! (event._1, event._3) // send type of topic and payload

}

object SubchannelBusImpl {

  trait TopicType

  object TopicType {

    case object Control extends TopicType {
      override def toString: String = "/control"
    }

    case object Payload extends TopicType {
      override def toString: String = "/payload"
    }

    case object Image extends TopicType {
      override def toString: String = "/image"
    }

  }

  trait ControlMessages

  object ControlMessages {

    case object Enable extends ControlMessages {
      override def toString: String = "enable"
    }

    case object Disable extends ControlMessages {
      override def toString: String = "disable"
    }

  }

}
