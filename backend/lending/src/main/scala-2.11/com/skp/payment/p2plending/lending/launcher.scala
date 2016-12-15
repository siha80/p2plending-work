package com.skp.payment.p2plending.lending

import java.util.concurrent.TimeUnit

import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.skp.payment.p2plending._
import com.skp.payment.p2plending.lending.service.{Reaper, ReaperFactory}
import com.skp.payment.p2plending.lending.ui.account.AccountGrpc
import com.skp.payment.p2plending.lending.ui.{AccountApi, HttpListener}
import com.skplanet.jose.JoseHeader.JoseHeaderKeySpec
import com.skplanet.jose._
import com.typesafe.config.ConfigFactory
import io.grpc.ServerCall.Listener
import io.grpc._
import io.grpc.netty.NettyServerBuilder

import scala.concurrent.Await
import scala.concurrent.duration._

object launcher extends App {
  implicit val dispatcher = system.dispatcher
  implicit val mat: ActorMaterializer = ActorMaterializer()

  val logger: LoggingAdapter = Logging(system, getClass)
  val config = ConfigFactory.load().getConfig(s"com.skp.p2plending.api")

  private var server: Server = null
  private val reaper = ReaperFactory.getReaper()

  args.headOption match {
    case Some("GRPC") =>
      try {
        server = NettyServerBuilder.forPort(config.getInt("port"))
                                  .addService(ServerInterceptors.intercept(AccountGrpc.bindService(new AccountApi, dispatcher), authenticateInterceptor))
                                  .build().start()
      } catch {
        case e: Exception =>
          logger.error(s"Failed to bind to port ${config.getInt("port")}", e)
          sys.exit
      }

      sys.addShutdownHook{
        logger.info("Actor Terminating ...")
//        reaper ! printRegistered
        reaper ! Reaper.Shutdown
        Await.ready(system.whenTerminated, 30 second)

        logger.info("Server Terminating ...")
        server.shutdown()
        server.awaitTermination(30L, TimeUnit.SECONDS)
      }
    case _ =>
      val listener = new HttpListener
      val binding = Http().bindAndHandle(handler = logRequestResult("log")(listener.lendingRoutes),
        interface = config.getString("interface"),
        port = config.getInt("port"))

      binding onFailure {
        case e: Exception =>
          logger.error(s"Failed to bind to port ${config.getInt("port")}", e)
          sys.exit
      }

      sys.addShutdownHook{
        logger.info("Terminating ...")

        reaper ! Reaper.Shutdown
        Await.ready(system.whenTerminated, 30 second)

        logger.info("Terminated ...")
      }
  }

  logger.info(s"Server started, listening on ${config.getInt("port")}")

  def authenticateInterceptor: ServerInterceptor = {
    val AUTHORIZATION_HEADER_KEY: Metadata.Key[String] =
      Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);

    val BEARER_PREFIX = "Bearer "

    new ServerInterceptor() {
      override def interceptCall[ReqT, RespT](serverCall: ServerCall[ReqT, RespT],
                                              metadata: Metadata,
                                              serverCallHandler: ServerCallHandler[ReqT, RespT]): Listener[ReqT] = {
//        import scala.collection.JavaConversions._
//        for (key <- metadata.keys()) {
//          val metaDataKey = Metadata.Key.of(key.toString, Metadata.ASCII_STRING_MARSHALLER)
//          println(s"Header: $metaDataKey => ${metadata.get(metaDataKey)}")
//        }

        val authorizationOption = Option(metadata.get(AUTHORIZATION_HEADER_KEY))
        val payload = if (authorizationOption != None && authorizationOption.get.startsWith(BEARER_PREFIX)) {
          val token = authorizationOption.get.split(" ").last
          logger.info(s"token kid: ${getKid(token)}")

          try {
            Some(new Jose().configuration(
              JoseBuilders.compactDeserializationBuilder
                .serializedSource(token)
                .key("12345678901234567890123456789012")
            ).deserialization)
          } catch {
            case e: Exception => logger.warning("Token verification failure:", e)
              None
          }
        } else {
          logger.error("No Token")
          None
        }

        payload match {
          case None =>
            serverCall.close(Status.UNAUTHENTICATED.withDescription(s"Deny your token. $payload"), metadata)
          case Some(t) =>
            logger.info(s"Token: $t")
        }

        serverCallHandler.startCall(serverCall, metadata)
      }
    }
  }

  def getKid(token: String): String = new JoseHeader().setEncoded(token).getHeader(JoseHeaderKeySpec.KEY_ID)
}
