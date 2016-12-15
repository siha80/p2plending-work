package com.skp.payment.p2plending.user

import slick.driver.{JdbcProfile, MySQLDriver}
trait MysqlDBComponent {
  val driver: JdbcProfile
  import driver.api._
  val db: Database
}

trait MySQLDBImpl extends MysqlDBComponent {
  val driver = MySQLDriver
  val db = MySqlDB.connectionPool
}

object MySqlDB {
  import slick.driver.MySQLDriver.api._
  import launcher._

  val connectionPool = Database.forConfig("mysql", config)
}

