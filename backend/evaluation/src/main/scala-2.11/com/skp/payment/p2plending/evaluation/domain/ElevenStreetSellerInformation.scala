package com.skp.payment.p2plending.evaluation.domain

import java.sql.ResultSet

/**
 * Created by 1002375 on 16. 11. 18..
 */
case class ElevenStreetSellerInformation(
                                          sellerId: String,
                                          sellerSerialNumber: String,
                                          bizSellerClassfyCode: String,
                                          bizSellerClassfyName: String,
                                          representationName: String,
                                          basicAddressDescription: String,
                                          detailAddressDescription: String,
                                          zipCode: String,
                                          bizNo: String,
                                          sellerRegistDate: String,
                                          sellerSeceedDate: String,
                                          sellerStatusCode: String,
                                          sellerStatusName: String,
                                          sellerCreditGradeCode: String,
                                          sellerCreditGradeName: String,
                                          partnerGradeCode: String,
                                          partnerGradeName: String,
                                          validPeriodElapsedUserYn: String,
                                          partDate: String
                                          ) {
  def this(rs: ResultSet) = this(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
    rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10),
    rs.getString(11), rs.getString(12), rs.getString(13), rs.getString(14), rs.getString(15),
    rs.getString(16), rs.getString(17), rs.getString(18), rs.getString(19)
  )
};
