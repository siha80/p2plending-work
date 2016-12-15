package com.skp.payment.p2plending.user.domain

import com.skp.payment.p2plending.user.MysqlDBComponent

case class User(userSerialNumber: String, email: String, userName: String, password: Option[String], authenticationSalt: Option[String],
                ci: Option[String] = None, ciHash: Option[String] = None)

trait UserInformation { this: MysqlDBComponent =>
  import driver.api._

  class Users(tag: Tag) extends Table[User](tag, "TB_AUTH_MEMBER") {
    val userSerialNumber = column[String]("MBR_NO", O.PrimaryKey)
    val email = column[String]("EMAIL")
    val userName = column[String]("USER_NM")
    val password = column[Option[String]]("PWD_CHK_KEY")
    val authenticationSalt = column[Option[String]]("AUTH_SALT")
    val ci = column[Option[String]]("CI")
    val ciHash = column[Option[String]]("CI_HASH")

    def pk = primaryKey("PK_AUTH_MEMBER", (userSerialNumber, email))

    // Select
    def * = (userSerialNumber, email, userName, password, authenticationSalt, ci, ciHash) <> (User.tupled, User.unapply)
  }
  val userTableQuery = TableQuery[Users]
}
