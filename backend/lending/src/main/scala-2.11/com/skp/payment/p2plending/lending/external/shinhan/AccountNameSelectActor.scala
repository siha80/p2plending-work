package com.skp.payment.p2plending.lending.external.shinhan

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import akka.actor.{Actor, ActorLogging}
import akka.stream._
import akka.stream.scaladsl.{Source, Tcp}
import akka.util.ByteString
import com.skp.payment.p2plending._
import com.skp.payment.p2plending.lending.external.shinhan.protocol._
import com.skp.payment.p2plending.lending.ui.account.AuthenticationRequest
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.reflect.runtime.universe._

class AccountNameSelectActor extends Actor
  with ActorLogging
  with ProtocolHandler[AuthenticationRequest, ShinHanBiResponse[AccountNameSelect, AuthenticationRequest]] {

  implicit val system = context.system
  implicit val dispatcher = context.dispatcher
  implicit val mat = ActorMaterializer()

  val ip = ConfigFactory.load().getString(s"shinhan.tcp.ip")
  val port = ConfigFactory.load().getInt(s"shinhan.tcp.port")

  override def receive: Receive = {
    case request: AuthenticationRequest => {
      log.info(s"sender: ${sender()}")
      val requestByteString = encode(request)
      log.info(s"request: ${requestByteString.length} [${requestByteString.utf8String}]")

      try {
        implicit val timeout = 3 second
        val responseByteString = requestToShinHan(ip, port, requestByteString)
        log.info(s"response: ${responseByteString.length} [${responseByteString.utf8String}]")
        val response = decode(responseByteString, request)
        log.info(s"response: ${sender()}->$response")

        sender() ! response
      } catch {
        case ex: StreamTcpException => throw ShinHanConnectionException(s"connection fail: ${ip}:${port}")
        case ex: Exception => log.error(s"unknown connection exception: ${ex.toString}")
          throw ShinHanConnectionException(s"Unknown reason fail: ${ip}:${port}")
      }
    }
    case _ => throw new IllegalArgumentException()
  }

  override def encode(request: AuthenticationRequest): ByteString = {
    def getHeader: Array[Byte] = {
      implicit def getType: Type = typeOf[Header]

      val timeStampNo = LocalDateTime.now.format(DateTimeFormatter.ofPattern("yyMMddHHmm"))
      val companyCode = "001"
      val bankCode = "20"
      val requestCode = "0400"
      val taskCode = "400"
      val retryCount = 1
      val requestId = LocalDateTime.now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
      val requestDate = LocalDateTime.now.format(DateTimeFormatter.ofPattern("yyMMdd"))
      val requestTime = LocalDateTime.now.format(DateTimeFormatter.ofPattern("HHmmss"))

      val header = Header(timeStampNo, companyCode, bankCode, requestCode, taskCode, retryCount, requestId, requestDate, requestTime, "")
      getByte(header, HeaderField.fields)
    }

    def getBody: Array[Byte] = {
      implicit def getType: Type = typeOf[AccountNameSelect]
      val accountNameSelect = AccountNameSelect(request.bankCode, request.accountNo, request.socialNo, "", 0)
      log.info(s"AccountNameSelect request: $accountNameSelect")
      getByte(accountNameSelect, AccountNameSelectField.fields)
    }

    ByteString(getHeader ++ getBody)
  }

  override def decode(byteString: ByteString, request: AuthenticationRequest):
    ShinHanBiResponse[AccountNameSelect, AuthenticationRequest] = {

    val bytes: Array[Byte] = byteString.toArray
    def getHeader: Header = {
      val headerClassFactory = new ShinHanResponseProtocolHandler.ClassFactory[Header]
      headerClassFactory.buildWith(bytes, HeaderField.fields)
    }

    def getBody: AccountNameSelect = {
      val bodyClassFactory = new ShinHanResponseProtocolHandler.ClassFactory[AccountNameSelect]
      bodyClassFactory.buildWith(bytes.drop(HeaderField.getTotalLength()), AccountNameSelectField.fields)
    }

    ShinHanBiResponse(getHeader, getBody, request)
  }

  private def getByte[T <: ShinHanRequestProtocolHandler](t: T, fields: List[ShinHanField])(implicit ttype: Type): Array[Byte] = {
    var bytes: Array[Byte] = Array.empty
    try {
      bytes = t.getBytes(fields)
    } catch {
      case e: IllegalArgumentException => e.printStackTrace()
    }

    return bytes
  }

  private def requestToShinHan(host: String, port: Int, data: ByteString)(implicit atMost: Duration): ByteString = {
    Await.result(Source(List(data))
      .via(Tcp().outgoingConnection(host, port))
      .runFold(ByteString.empty)(_ ++ _), atMost)
  }
}
