package com.skp.payment.p2plending.lending.external.kafka

import akka.actor.ActorSystem
import akka.kafka.javadsl.Producer
import akka.kafka.{ProducerMessage, ProducerSettings}
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import com.skp.payment.p2plending._
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}
import org.json4s.jackson

import scala.concurrent.Future
import scala.reflect.runtime.{universe => ru}

trait KafkaPublisher {
  implicit val serialization = jackson.Serialization // or native.Serialization
  /**
   *
   * @param topic             : topic for message
   * @param message           : sending message
   * @param partition         : partition for message
   * @param system            : implicit ActorSystem
   * @param mat               : implicit Materializer
   * @return                  : Funture[KafkaPublishResult]
   */
  def publish(topic: String, message: String, partition: Int = 0)(implicit system: ActorSystem, mat: Materializer): Future[KafkaPublishResult] = {
    val bootstrapServers = ConfigFactory.load().getString(s"com.skp.p2plending.kafka.bootstrapServers")

    val producerSettings = ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
      .withBootstrapServers(bootstrapServers)

    Source(List(message))
      .map(message => ProducerMessage.Message(new ProducerRecord[Array[Byte], String](topic, partition, null, message.toString), message))
      .via(Producer.flow(producerSettings))
      .map { result =>
      val record = result.message.record
      KafkaPublishResult(record.topic, record.partition, result.offset, record.value)
    }.runWith(Sink.head)
  }

//  def publish[T](topic: String, obj: T, partition: Int = 0)(implicit system: ActorSystem, mat: Materializer): Future[KafkaPublishResult] = {
//    isCaseClass(obj) match {
//      case true =>
//        val message: String = ""
//        publish(topic, message, partition)
//      case false => throw new IllegalArgumentException("only publish to case classes")
//    }
//  }
//
//  private def isCaseClass[T](obj: T): Boolean = {
//    val tpe = getTypeTag(obj).tpe
//    val classSymbol = tpe.typeSymbol.asClass
//    !(tpe <:< typeOf[Product] && classSymbol.isCaseClass)
//  }
//
//  private def getTypeTag[T: ru.TypeTag](obj: T) = ru.typeTag[T]
}
