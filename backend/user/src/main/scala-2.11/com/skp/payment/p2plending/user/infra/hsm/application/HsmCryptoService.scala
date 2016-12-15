package com.skp.payment.p2plending.user.infra.hsm.application

import com.skp.payment.p2plending.user.infra.TransferClient
import com.skp.payment.p2plending.user.infra.hsm.ui.{HsmJsonResponse, AliasRequest}
import com.typesafe.config.{Config}

import org.json4s._
import org.slf4j.Logger

trait HsmCryptoService extends TransferClient {
  implicit val logger: Logger
  implicit val formats = DefaultFormats

  def config: Config
  lazy val hsmConfig = config.getConfig("hsm")

  def encryptionAes256WithAlias(alias: String, target: String): Option[String] = {
    val data = AliasRequest(alias, target)
    val response =
      jackson.Serialization.read[HsmJsonResponse](
        postRawDataOnHttps(hsmConfig.getString("host"), "/v1/hsm/encryption/aes256", hsmConfig.getInt("port"), hsmConfig.getBoolean("ssl"), jackson.Serialization.write(data)))
    response.errorCode match  {
      case Some(error) => None
      case _ => response.output
    }
  }

  def decryptionAes256WithAlias(alias: String, target: String): Option[String] = {
    val data = AliasRequest(alias, target)
    val response =
      jackson.Serialization.read[HsmJsonResponse](
        postRawDataOnHttps(hsmConfig.getString("host"), "/v1/hsm/decryption/aes256", hsmConfig.getInt("port"), hsmConfig.getBoolean("ssl"), jackson.Serialization.write(data)))
    response.errorCode match  {
      case Some(error) => None
      case _ => response.output
    }
  }
}
