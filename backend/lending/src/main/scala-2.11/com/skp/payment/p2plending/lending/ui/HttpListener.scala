package com.skp.payment.p2plending.lending.ui

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives._
import com.skp.payment.p2plending.lending.ui.account.AuthenticationRequest
import org.json4s.{DefaultFormats, jackson}

import Json4sSupport._

class HttpListener {
  implicit val serialization = jackson.Serialization // or native.Serialization

  implicit val format = DefaultFormats
  val lendingRoutes = pathPrefix("v1") {
    path("account" / "authentication") {
      entity(as[AuthenticationRequest]) { request =>
        post {
          complete(ToResponseMarshallable(new AccountApi().authentication(request)))
        }
      }
    }
  }

}
