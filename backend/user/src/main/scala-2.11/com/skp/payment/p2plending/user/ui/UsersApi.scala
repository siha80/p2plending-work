package com.skp.payment.p2plending.user.ui

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import com.skp.payment.p2plending.user.application.UserService
import com.skp.payment.p2plending.user.domain.UserInformationRepository
import com.typesafe.config.Config
import org.json4s.{jackson}
import Json4sSupport._
import com.skp.payment.p2plending.user.Authentication.authenticateWithToken
import org.slf4j.{LoggerFactory, Logger}
import scala.concurrent.Await
import scala.language.postfixOps
import scala.concurrent.duration._

trait UsersApi extends UserService {
  implicit val serialization = jackson.Serialization
  val logger: Logger = LoggerFactory.getLogger(this.getClass)
  implicit val config: Config

  val usersRoutes = pathPrefix("v1") {
    authenticateWithToken() { token =>
      path("users" / "authentication") {
        (post & entity(as[LoginRequestEvent])) { request =>
          complete(ToResponseMarshallable(authentication(request)))
        }
      } ~
      path("users" / "signup") {
        (post & entity(as[SignupRequestEvent])) { request =>
          complete {
            val res = signUp(request)
            HttpResponse(
              status = res.error match {
                case Some(error) => StatusCodes.BadRequest
                case _ => StatusCodes.Created
              },
              entity = res
            )
          }
        }
      } ~
      path("users" / "email" / "exist") {
        (get & parameters('email)) { email =>
          complete {
            findByEmail(email) match {
              case Some(user) => HttpResponse(status = StatusCodes.OK)
              case _ => HttpResponse(status = StatusCodes.NotFound)
            }
          }
        }
      } ~
      path("users" / "password" / "change") {
        (post & entity(as[ChangePasswordRequest])) { request =>
          complete {
            "OK"
          }
        }
      } ~
      path("users" / "test" / "ids") {
        complete {
          logger.info("TEST: IDS: LIST")
          ToResponseMarshallable {
            Await.result(
              UserInformationRepository.findByIds(Set("b4a7d178-6bfc-4d9f-8f63-6f0503ed2979", "c39b2e52-1216-4b5a-96d6-4bb78b26f144")), 3 seconds)
          }
        }
      }
    }
  }
}