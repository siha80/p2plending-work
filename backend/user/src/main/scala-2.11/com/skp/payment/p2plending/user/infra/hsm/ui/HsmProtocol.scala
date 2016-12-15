package com.skp.payment.p2plending.user.infra.hsm.ui

/**
 * Created by 1002375 on 16. 11. 9..
 */
case class AliasRequest(alias: String, target: String)
case class KeyRequest(key: String, target: String)
case class HsmJsonResponse(output: Option[String], errorCode: Option[String])

class HsmProtocol {
}
