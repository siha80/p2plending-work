package com.skp.payment.p2plending.lending.external.shinhan

import com.skp.payment.p2plending.lending.external.shinhan.protocol.Header

case class ShinHanResponse[T](header: Header, body: T)
case class ShinHanBiResponse[T1, T2](header: Header, body: T1, request: T2)
