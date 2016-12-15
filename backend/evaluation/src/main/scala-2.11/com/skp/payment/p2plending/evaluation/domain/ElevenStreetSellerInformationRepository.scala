package com.skp.payment.p2plending.evaluation.domain

import com.skp.payment.p2plending.evaluation.QueryCacheImpl
import org.slf4j.{LoggerFactory, Logger}

import scala.collection.immutable.IndexedSeq

trait ElevenStreetSellerInformationRepository { this:QueryCacheImpl =>
  val logger: Logger = LoggerFactory.getLogger(this.getClass)
  def findAll(): List[ElevenStreetSellerInformation] = {
    select[ElevenStreetSellerInformation](

    "SELECT SLLR_ID, sllr_mbr_no, biz_mbr_clsf_cd, biz_mbr_clsf_nm, rep_nm, basic_addr_desc, " +
     "dtl_addr_desc, mail_no, biz_no, sllr_rege_dt, sllr_scsn_dt, mbr_stat_cd, mbr_stat_nm, " +
      "sell_crdt_grd_cd, sell_crdt_grd_nm, ptnr_grd_cd, ptnr_grd_nm, valid_perd_elap_mbr_yn, part_date " +
       "FROM 11st.tb_evs_dw_m_sllr_basic_info " +
        "where part_date like '201612%' limit 10",
      rs => new ElevenStreetSellerInformation(rs)
    )
  }

  def query: List[Map[String, String]] = {
    val qry: String = "SELECT sell_crdt_grd_cd, COUNT(1) AS COUNT FROM 11st.tb_evs_dw_m_sllr_basic_info " +
      "where mbr_stat_cd = '01' and PART_DATE like '201612%' GROUP BY sell_crdt_grd_cd"
    select(qry, rs => {
        val values: IndexedSeq[(String, String)] = for(i <- 1 to rs.getMetaData.getColumnCount)
          yield (rs.getMetaData.getColumnName(i), rs.getString(i))
        values.toMap[String, String]
      }
    )
  }
}

object ElevenStreetSellerInformationRepository extends ElevenStreetSellerInformationRepository with QueryCacheImpl
