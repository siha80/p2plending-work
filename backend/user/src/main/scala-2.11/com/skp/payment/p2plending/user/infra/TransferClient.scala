package com.skp.payment.p2plending.user.infra

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{MediaTypes, ContentType}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import akka.stream.scaladsl.{Tcp, Sink, Source, Flow}
import akka.util.ByteString

import scala.concurrent.{Future, Await, ExecutionContext}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

trait TransferClient {
  implicit val system: ActorSystem
  implicit val executor: ExecutionContext
  implicit val materializer: Materializer

  def postRawDataOnHttps(host: String, uri: String, port: Int, isSsl: Boolean, data: String): String = {
    val httpFlow = isSsl match {
      case true => Http().outgoingConnectionHttps(host, port)
      case _ => Http().outgoingConnection(host, port)
    }

    Await.result(Source.single(
      RequestBuilding
        .Post(uri)
        .withEntity(ContentType(MediaTypes.`application/json`), data)
        .addHeader(RawHeader("Accept", "application/json"))
      ).via(httpFlow).runWith(Sink.head).flatMap(response => Unmarshal(response.entity).to[String]), 3 seconds
    )
  }
  
  def sendDataOnTcp(host: String, port: Int, data: ByteString)(implicit atMost: Duration): ByteString = {
    Await.result(Source(List(data))
      .via(Tcp().outgoingConnection(host, port))
      .runFold(ByteString.empty)(_ ++ _), atMost)
  }
}
