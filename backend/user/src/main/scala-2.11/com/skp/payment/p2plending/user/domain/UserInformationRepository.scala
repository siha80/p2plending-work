package com.skp.payment.p2plending.user.domain

import com.skp.payment.p2plending.user.{MySQLDBImpl, MysqlDBComponent}
import slick.driver

import scala.concurrent.Future


trait UserInformationRepository extends UserInformation { this:MysqlDBComponent =>
  import driver.api._

  def create(user: User): Future[Int] = db.run { userTableQuery += user }

  def update(user: User): Future[Int] = db.run { userTableQuery.filter(_.userSerialNumber === user.userSerialNumber).update(user) }

  def delete(userSerialNumber: String): Future[Int] = db.run { userTableQuery.filter(_.userSerialNumber === userSerialNumber).delete }

  def findById(userSerialNumber: String): Future[Option[User]] = db.run {
    userTableQuery.filter(_.userSerialNumber === userSerialNumber).result.headOption
  }

  def findByIds(userSerialNumberList: Set[String]): Future[List[User]] = db.run {
    userTableQuery.filter(_.userSerialNumber inSet(userSerialNumberList)).to[List].result
  }

  def findByEmail(email: String): Future[Option[User]] = db.run( userTableQuery.filter(_.email === email).result.headOption )

  def getAll(): Future[List[User]] = db.run( userTableQuery.to[List].result )
}

object UserInformationRepository extends UserInformationRepository with MySQLDBImpl
