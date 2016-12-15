package com.skp.payment.p2plending.lending.service

import akka.actor.{ActorRef, Props}
import com.skp.payment.p2plending._
import org.slf4j.LoggerFactory

import scala.collection.mutable.ArrayBuffer

object ReaperFactory {
  private val reapers = ArrayBuffer.empty[ActorRef]
  val logger = LoggerFactory.getLogger(this.getClass)

  private def createReaper(): ActorRef = {
    val reaperActor = system.actorOf(Props[Reaper], "Reaper")
    reapers += reaperActor

    logger.info("Reaper stared")

    reaperActor
  }

  def getReaper(): ActorRef = {
    val reaper = reapers.headOption match {
      case None => createReaper()
      case Some(ref) => ref
    }

    reaper
  }
}
