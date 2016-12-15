package com.skp.payment.p2plending.evaluation.domain

/**
 * Created by 1002375 on 16. 11. 16..
 */
case class Token(iss: String, aud: String, typ: String, iat: Long, exp: Long)
