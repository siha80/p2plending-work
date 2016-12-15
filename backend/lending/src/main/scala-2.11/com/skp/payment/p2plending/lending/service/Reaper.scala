package com.skp.payment.p2plending.lending.service

import akka.actor._
import akka.pattern.gracefulStop
import scala.concurrent.duration._

import scala.collection.mutable.ArrayBuffer

import com.skp.payment.p2plending._

object Reaper {
  case class WatchMe(ref: ActorRef)
  object Shutdown
  object printRegistered
}

class Reaper extends Actor with ActorLogging {
  import Reaper._
  private val watched = ArrayBuffer.empty[ActorRef]

  override def receive: Receive = {
    case WatchMe(ref) =>
      context.watch(ref)
      watched += ref
      log.info(s"${getActorPath(ref)} registered by reaper")
    case Terminated(ref) =>
      watched -= ref
      log.info(s"${getActorPath(ref)} is terminated")

      if (watched.isEmpty) {
        context.system.terminate()
        log.info(s"$actorSystemName is terminate")
      }
    case Shutdown =>
      log.info("shutdown start!!")

      val f = for {
        ref <- watched
        stoped = gracefulStop(ref, 3 seconds)
      } yield (ref, stoped)

      f.foreach(x => log.info(s"${getActorPath(x._1.asInstanceOf[ActorRef])} is stop"))
    case printRegistered =>
      log.info(s"registered actor: ${watched.map(x => getActorPath(x)).mkString(", ")}")
  }

  def getActorPath(ref: ActorRef) = ref.path.name
}
