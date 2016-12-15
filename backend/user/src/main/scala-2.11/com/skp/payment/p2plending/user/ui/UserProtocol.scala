package com.skp.payment.p2plending.user.ui

import akka.http.scaladsl.model.StatusCode

case class ErrorResponse(status: StatusCode, code: String, message: Option[String])
case class UserResponse(userSerialNumber: String, email: String, userName: String)
case class SignupResponseEvent(user: Option[UserResponse] = None, error: Option[ErrorResponse] = None)
case class SignupRequestEvent(userName: String, email: String, password: String)
case class LoginRequestEvent(email: String, password: String)
case class LoginResponseEvent(isValid: Boolean, userSerialNumber: String)
case class ChangePasswordRequest(userSerialNumber: String, password: String)

case class SecedeResponse(error: Option[ErrorResponse] = None)
case class SecedeRequest(userSerialNumber: String, password: String)
