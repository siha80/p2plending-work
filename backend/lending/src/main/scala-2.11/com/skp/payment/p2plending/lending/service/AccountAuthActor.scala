package com.skp.payment.p2plending.lending.service

import java.util.concurrent.TimeoutException

import akka.actor.SupervisorStrategy._
import akka.actor._
import akka.event.LoggingReceive
import akka.pattern.ask
import akka.util.Timeout
import com.skp.payment.p2plending.lending.external.shinhan.protocol.AccountNameSelect
import com.skp.payment.p2plending.lending.external.shinhan.{AccountNameSelectActor, ShinHanBiResponse, ShinHanConnectionException}
import com.skp.payment.p2plending.lending.ui.account.{AuthenticationRequest, AuthenticationResponse}

import scala.concurrent.Await
import scala.concurrent.duration._

class AccountAuthActor extends Actor with GracefulShutdown with ActorLogging {
  implicit val dispatcher = context.dispatcher
  val shinHanRequestActorRef = context.actorOf(Props[AccountNameSelectActor], "AccountNameSelectHandler")

  shutdownRegister

  override val supervisorStrategy = OneForOneStrategy(loggingEnabled = false) {
    case e: ShinHanConnectionException => log.error(e.message)
      Resume
  }

  override def shutdownRegister(): Unit = {
    watchMe(self)
  }

  override def receive = LoggingReceive {
    case request: AuthenticationRequest => {
      log.info(s"sender: ${sender()}")
      implicit val soTimeout = Timeout(5 second)

      val f = shinHanRequestActorRef ? request
      try {
        val response = Await.result(f, soTimeout.duration)
          .asInstanceOf[ShinHanBiResponse[AccountNameSelect, AuthenticationRequest]]

        log.debug(s"response: $f")
        log.debug(s"sender(): ${sender()}")

        sender() ! AuthenticationResponse("9999", responseHandler(response))
      } catch {
        case e: TimeoutException => sender() ! AuthenticationResponse("9998", "신한은행 응답 지연으로 계좌인증에 실패하였습니다.")
      }
    }
  }

  def responseHandler(response: ShinHanBiResponse[AccountNameSelect, AuthenticationRequest]): String = {
    val header = response.header
    val accountNameSelect = response.body
    val requestEvent = response.request

    log.info(s"checkName: ${requestEvent.ownerName} and ${accountNameSelect.ownerName}")
    if (requestEvent.ownerName == accountNameSelect.ownerName) "SUCCESS"
    else "계좌인증에 실패하였습니다. 본인계좌정보를 정확히 입력하시기 바랍니다."
  }
}

