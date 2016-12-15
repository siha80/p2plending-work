package com.skp.payment.p2plending.lending.service

import akka.actor.ActorRef
import com.skp.payment.p2plending.lending.service.Reaper.WatchMe

/**
 * Created by byeongchan.park@sk.com(1000808) on 2016-11-30.
 */
trait GracefulShutdown {
  val reaper = ReaperFactory.getReaper()

  def watchMe(ref: ActorRef):Unit = {
    reaper ! WatchMe(ref)
  }

  def shutdownRegister(): Unit
}
