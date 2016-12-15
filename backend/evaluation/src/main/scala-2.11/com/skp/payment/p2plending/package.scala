package com.skp.payment

package object p2plending {
  val profile: String = Option(System.getProperty("akka.profile")) match {
    case Some(arg) => arg
    case None => "local"
  }
}
