package com.skp.payment

import akka.actor.ActorSystem

package object p2plending {
  val profile: String = Option(System.getProperty("akka.profile")) match {
    case Some(arg) => arg
    case None => "local"
  }

  val actorSystemName = "p2p-lending"

  // ActorSystem is a heavy object: create only one per application
  implicit val system = ActorSystem(actorSystemName)
}
