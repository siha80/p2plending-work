package com.skp.payment.p2plending.evaluation

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directive1, Directives}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.skp.payment.p2plending._
import com.skp.payment.p2plending.evaluation.domain.Token
import com.skp.payment.p2plending.evaluation.ui.EvaluationApi
import com.skplanet.jose.{JoseBuilders, Jose}
import com.skplanet.jose.commons.codec.binary.Base64
import com.typesafe.config.ConfigFactory
import org.json4s.{DefaultFormats, jackson}
import org.slf4j.{LoggerFactory, Logger}

import scala.concurrent.ExecutionContext

object launcher extends App with EvaluationApi {
  implicit val system = ActorSystem()
  implicit val executor: ExecutionContext = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  override val logger: Logger = LoggerFactory.getLogger(this.getClass)
  val config = ConfigFactory.load(
    profile match {
      case "local" => "application.conf"
      case p => s"application.$p.conf"
    }
  ).getConfig("com.skp.p2plending")
  try {
    val httpConfig = config.getConfig("http")

    Http().bindAndHandle(handler = logRequestResult("log")(estimateRoutes), interface = httpConfig.getString("interface"), port = httpConfig.getInt("port.evaluation"))
  } catch {
    case e: Exception => logger.error("Server start error...", e)
  }
}

object Authentication extends Directives {
  val TOKEN_KEY = "12345678123456781234567812345678"
  def authenticateWithToken(): Directive1[Token] = {
    implicit val formats = DefaultFormats
    optionalHeaderValueByName("Authorization") flatMap {
      case Some(authHeader) =>
        val accessToken = authHeader.split(' ').last
        val token = jackson.Serialization.read[Token](ByteString(Base64.decodeBase64(accessToken.split('.')(1))).utf8String)
        try {
          new Jose().configuration(JoseBuilders.compactDeserializationBuilder()
            .serializedSource(accessToken)
            .key(TOKEN_KEY)
          ).deserialization() match {
            case t: String => provide(token)
            case _ => complete(StatusCodes.Unauthorized, "Forbidden")
          }
        } catch {
          case e: Exception =>
            complete(StatusCodes.Unauthorized, "Forbidden")
        }
      case _ => complete(StatusCodes.Unauthorized, "Forbidden")
    }
  }
}
