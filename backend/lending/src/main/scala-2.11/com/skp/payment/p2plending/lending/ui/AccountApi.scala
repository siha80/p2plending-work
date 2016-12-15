package com.skp.payment.p2plending.lending.ui

import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import com.skp.payment.p2plending._
import com.skp.payment.p2plending.lending.service.AccountAuthActor
import com.skp.payment.p2plending.lending.ui.account.AccountGrpc.Account
import com.skp.payment.p2plending.lending.ui.account.{AuthenticationRequest, AuthenticationResponse}

import scala.concurrent.Future

class AccountApi extends Account {
  val service = system.actorOf(Props[AccountAuthActor], "AccountAuthService")

  override def authentication(request: AuthenticationRequest): Future[AuthenticationResponse] = {
    import scala.concurrent.duration._

    implicit val timeout = Timeout(10 second)
    (service ? request).mapTo[AuthenticationResponse]
  }
}
