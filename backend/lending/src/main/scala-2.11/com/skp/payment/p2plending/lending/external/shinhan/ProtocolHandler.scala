package com.skp.payment.p2plending.lending.external.shinhan

import akka.util.ByteString

trait ProtocolHandler[T, R] {
  def encode(t: T): ByteString
  def decode(byteString: ByteString, t: T): R
}
