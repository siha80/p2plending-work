package com.skp.payment.p2plending.user.application

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import com.skp.payment.p2plending.secruity.UserPasswordEncoder
import com.skp.payment.p2plending.user.domain.{User, UserInformationRepository}
import com.skp.payment.p2plending.user.infra.TransferClient
import com.skp.payment.p2plending.user.infra.hsm.application.HsmCryptoService
import com.skp.payment.p2plending.user.ui._
import org.slf4j.Logger

import scala.concurrent.{Await}
import scala.concurrent.duration._

trait UserService extends HsmCryptoService with TransferClient {
  implicit val logger: Logger
  val userPasswordEncoder = new UserPasswordEncoder

  def authentication(request: LoginRequestEvent): LoginResponseEvent = {
    val user = Await.result(UserInformationRepository.findByEmail(request.email), 3.seconds)
    user match {
      case Some(user) => LoginResponseEvent(isPasswordValid(user.password.get, request.password, user.authenticationSalt.get), user.userSerialNumber)
      case _ => LoginResponseEvent(false, null)
    }
  }

  def list: List[User] = {
    Await.result(UserInformationRepository.getAll(), 5.seconds)
  }

  def findById(id: String) = {
    Await.result(UserInformationRepository.findById(id), 1.seconds)
  }

  def findByEmail(email: String) = {
    Await.result(UserInformationRepository.findByEmail(email), 1.seconds)
  }

  def signUp(request: SignupRequestEvent) = {
    val userSerialNumber = UUID.randomUUID().toString;
    try {
      Await.result(UserInformationRepository.create(createWith(request, userSerialNumber)), 3 seconds)
    } catch {
      case e: Exception => logger.error(s"${e}")
    }

    Await.result(UserInformationRepository.findById(userSerialNumber), 1 seconds) match {
      case Some(user) => SignupResponseEvent(Some(UserResponse(user.userSerialNumber, user.email, request.userName)))
      case _ => SignupResponseEvent(error = Some(ErrorResponse(StatusCodes.BadRequest, "SIGN_FAILED", Some("SIGN_FAILED"))))
    }
  }


  def secede(request: SecedeRequest): SecedeResponse = {
    findById(request.userSerialNumber) match {
      case Some(user) => {
        if(isPasswordValid(user.password.get, request.password, user.authenticationSalt.get)) {
          UserInformationRepository.delete(user.userSerialNumber)
          SecedeResponse()
        } else {
          SecedeResponse(Some(ErrorResponse(StatusCodes.BadRequest, "NOT_MATCH_PASSWORD", Some("NOT_MATCH_PASSWORD"))))
        }
      }
      case _ => SecedeResponse(Some(ErrorResponse(StatusCodes.BadRequest, "NOT_FOUND_USER", Some("NOT_FOUND_USER"))))
    }
  }

  protected def createWith(request: SignupRequestEvent, userSerialNumber: String ): User = {
    val userName = encryptionAes256WithAlias(hsmConfig.getString("common.alias"), request.userName).get
    val salt = userPasswordEncoder.getSalt(32)
    User(userSerialNumber, request.email, userName, Some(userPasswordEncoder.encodePassword(request.password, salt)), Some(salt))
  }

  protected def isPasswordValid(userPassword: String, reqPassword: String, salt: String): Boolean = {
    val result = userPasswordEncoder.isPasswordValid(userPassword, reqPassword, salt)
    logger.info(s"IS VALID: ${result}")
    result
  }
}
