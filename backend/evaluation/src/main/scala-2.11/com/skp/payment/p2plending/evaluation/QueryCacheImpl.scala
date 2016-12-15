package com.skp.payment.p2plending.evaluation

import java.sql.ResultSet
import com.typesafe.config.ConfigFactory
import org.apache.commons.dbcp2.BasicDataSource

object Datasource {
  import launcher._

  val querycacheConfig = config.getConfig("querycache")

  val dbUrl = querycacheConfig.getString("url")
  val connectionPool = new BasicDataSource

  connectionPool.setUsername(querycacheConfig.getString("user"))
  connectionPool.setPassword(querycacheConfig.getString("password"))
  connectionPool.setDriverClassName("com.skplanet.querycache.jdbc.QCDriver")
  connectionPool.setUrl(dbUrl)
  connectionPool.setInitialSize(querycacheConfig.getInt("numThreads"))
}

trait QueryCacheImpl {
  implicit def rsToStream(rs: ResultSet) = new Iterator[ResultSet] {
    override def hasNext: Boolean = rs.next()
    override def next(): ResultSet = rs
  }

  def select[A](query: String, mapper: ResultSet => A): List[A] = {
    val conn = Datasource.connectionPool.getConnection
    val stmt = conn.createStatement();
    val rs = stmt.executeQuery(query);

    rs.map(mapper).toList
  }
}
