//package aview
//
//import akka.actor.ActorSystem
//import akka.kafka.ProducerSettings
//import akka.kafka.scaladsl.Producer
//import akka.stream.scaladsl.Source
//import org.apache.kafka.clients.producer.ProducerRecord
//import org.apache.kafka.common.serialization.StringSerializer
//
//object AkkaKafkaUIProducer {
//  implicit val system: ActorSystem = ActorSystem("KafkaProducerSystem")
//
//  val bootstrapServers =
//    if (sys.env.get("RUNNING_IN_DOCKER").contains("true")) "kafka:9092" else "localhost:29092"
//
//  val producerSettings = ProducerSettings(system, new StringSerializer, new StringSerializer)
//    .withBootstrapServers(bootstrapServers)
//
//  def sendMessages(messages: Seq[String]): Unit = {
//    Source(messages)
//      .map { msg =>
//        println(s"\u001b[32mSend\u001b[0m: $msg")
//        new ProducerRecord[String, String]("controller", msg)
//      }
//      .runWith(Producer.plainSink(producerSettings))
//  }
//}