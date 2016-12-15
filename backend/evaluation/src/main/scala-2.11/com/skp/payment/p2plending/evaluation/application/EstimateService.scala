package com.skp.payment.p2plending.evaluation.application

import com.skp.payment.p2plending.evaluation.domain.ElevenStreetSellerInformationRepository

trait EstimateService {
  def requestEstimate = {
  }

  def findAllSeller() = {
    ElevenStreetSellerInformationRepository.findAll()
  }

  def query = {
    ElevenStreetSellerInformationRepository.query
  }
}
