# Event Bus Playground

A practical example on how to implement a pub/sub system with Akka EventBus that illustrates how it is possible to build a distributed computer vision system in Scala. Here only concept of task distribution is covered.

For those coming from ROS node the task might be trivial. In Akka it appears to be trivial too, but it takes some time to get used to Akka. 

Let's say we have several cameras and we need to carry out the following operations: receive images from cameras, write images on disk and display them in a visualization program. The main difficulty appears when one needs to toggle writing and displaying while the system works. With plain actors this becomes over complicated pretty quickly. Event bus comes to the rescue!

There are four actors in the system: 

1. LeaderActor - the one who creates other actors and  wires everything up; 
2. CameraActor - source of images; 
3. WriterActor - should write images on disk; 
4. VisActor - visualizes images in a GUI.

The CameraActor represents a camera and uses a scheduler to read images from a device (this approach may be useful if OpenCV is used; or a camera driver can issue callbacks when camera finishes reading an image then no scheduler is needed). This actor publishes on topic `/camera/image`.

The WriterActor and VisActor receive images and carry out their respective tasks. 

Also, all the actors subscribe to control topics of type `/actor-type/control`.

With the pub/sub approach it is possible to broadcast messages to subscribers, namely once a CameraActor publishes an image, it is fed straight away to WriterActor and VisActor. Also, it is possible to toggle state of all the WriterActors and VisActors with a single "publish" command.

## References

Thanks to the authors of the following articles that helped me to find the perfect solution!

[Pub/sub and Akka EventStream](https://web.archive.org/web/20150826074648/http:/www.benhowell.net/examples/2014/04/18/scala-and-the-akka-eventstream/)

[Pub/sub and Akka EventBus](https://web.archive.org/web/20150829002005/http:/www.benhowell.net/examples/2014/04/23/scala_and_the_akka_event_bus)

[Peer-to-Many Communication in Akka](https://danielasfregola.com/2015/04/20/peer-to-many-communication-in-akka/)

