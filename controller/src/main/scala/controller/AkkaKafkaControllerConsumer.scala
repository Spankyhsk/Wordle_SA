package controller

import akka.actor.ActorSystem
import akka.kafka.ConsumerSettings
import akka.kafka.Subscriptions
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.Sink
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer

object AkkaKafkaControllerConsumer {
  implicit val system: ActorSystem = ActorSystem("KafkaConsumerSystem")

  val bootstrapServers =
    if (sys.env.get("RUNNING_IN_DOCKER").contains("true")) "kafka:9092" else "localhost:29092"

  val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers(bootstrapServers)
    .withGroupId("tui-group")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  def consume(): Unit = {
    Consumer
      .plainSource(consumerSettings, Subscriptions.topics("controller"))
      .map(record => println(s"\u001b[32mReceived\u001b[0m: ${record.value()}"))
      .runWith(Sink.ignore)
  }
}