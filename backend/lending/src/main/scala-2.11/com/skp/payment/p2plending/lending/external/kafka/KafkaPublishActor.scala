package com.skp.payment.p2plending.lending.external.kafka

import akka.actor._
import akka.stream.ActorMaterializer

import scala.concurrent.Future
import scala.util.{Failure, Success}

case class KafkaPublishMessage(topic: String, message: String)
case class KafkaPublishResult(topic: String, partition: Int, offset: Long, message: String)

/**
 * kafka publish 처리 actor class
 * publish 결과에 대한 receiver actor 설정이 가능하다.
 *
 * @param successListener     : 성공인 경우 처리 actor ex) 히스토리 저장 등
 * @param failureListener     : 실패인 경우 처리 actor ex) 재전송 배치를 취한 publish 데이터 저장 등
 */
class KafkaPublishActor(successListener: Option[ActorRef] = None, failureListener: Option[ActorRef] = None)
  extends Actor with ActorLogging with KafkaPublisher {

  implicit val system = context.system
  implicit val dispatcher = context.dispatcher
  implicit val mat = ActorMaterializer()

  override def receive: Receive = {
    case KafkaPublishMessage(topic, message) =>
      val f:Future[KafkaPublishResult] = publish(topic, message)
      f onComplete {
        case Success(result) => {
          log.info(s"Publish Success: ${result.toString}")

          successListener match {
            case Some(listener) => listener ! result
            case _ => log.info("No actor to handle success result")
          }
        }

        case Failure(e) =>
          log.error(s"KafkaProducerException: ${e.getMessage}")

          failureListener match {
            case Some(listener) => listener ! KafkaPublishMessage(topic, message)
            case _ => log.info("No actor to handle failure result")
          }
      }
  }
}
