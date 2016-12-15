package com.skp.payment.p2plending.lending.external.shinhan.protocol

object AccountNameSelectField extends AccountNameSelectField
trait AccountNameSelectField extends ShinHanRequestProtocolHandler {
  val fields = List(
    ShinHanField("bankCode",   FieldFormat.CHAR, 2),
    ShinHanField("accountNo",  FieldFormat.CHAR, 15),
    ShinHanField("socialNo",   FieldFormat.CHAR, 14),
    ShinHanField("ownerName",  FieldFormat.CHAR, 20),
    ShinHanField("amount",     FieldFormat.NUM, 13),
    ShinHanField("filler",     FieldFormat.CHAR, 86, Some("")))
}

case class AccountNameSelect(bankCode: String,
                             accountNo: String,
                             socialNo: String,
                             ownerName: String,
                             amount: Long,
                             filler: String = "") extends AccountNameSelectField {
}
