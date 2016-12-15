package com.skp.payment.p2plending.lending.external.shinhan.protocol

object HeaderField extends HeaderField
trait HeaderField extends ShinHanRequestProtocolHandler {
  val fields = List(
    ShinHanField("timeStampNo",   FieldFormat.CHAR, 9),
    ShinHanField("companyCode",   FieldFormat.CHAR, 8),
    ShinHanField("bankCode",      FieldFormat.CHAR, 2),
    ShinHanField("requestCode",   FieldFormat.CHAR, 4),
    ShinHanField("taskCode",      FieldFormat.CHAR, 3),
    ShinHanField("retryCount",    FieldFormat.NUM, 1),
    ShinHanField("requestId",     FieldFormat.CHAR, 6),
    ShinHanField("requestDate",   FieldFormat.CHAR, 6),
    ShinHanField("requestTime",   FieldFormat.CHAR, 6),
    ShinHanField("responseCode",  FieldFormat.CHAR, 4),
    ShinHanField("filler",        FieldFormat.CHAR, 51, Some("")))

  def getTotalLength() = fields.map(_.len).sum
}

case class Header(timeStampNo: String,
                  companyCode: String,
                  bankCode: String,
                  requestCode: String,
                  taskCode: String,
                  retryCount: Long,
                  requestId: String,
                  requestDate: String,
                  requestTime: String,
                  responseCode: String,
                  filler: String = "") extends HeaderField {
}
