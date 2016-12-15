package com.skp.payment.p2plending.evaluation.ui

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives._
import com.skp.payment.p2plending.evaluation.application.EstimateService
import org.json4s.{jackson, DefaultFormats}

import com.skp.payment.p2plending.evaluation.ui.Json4sSupport._
import org.slf4j.{LoggerFactory, Logger}

trait EvaluationApi extends EstimateService {
  implicit val serialization = jackson.Serialization // or native.Serialization
  implicit val format = DefaultFormats

  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  val estimateRoutes = pathPrefix("v1") {
    path("estimate" / "sellerinfo") {
      post {
        logger.info("SELLECT...")
        val seller = findAllSeller()
        logger.info(s"result: ${seller}")
        complete(ToResponseMarshallable(seller))
      }
    } ~
    path("evaluation" / "query") {
      post {
        complete(ToResponseMarshallable(query))
      }
    }
  }
}
