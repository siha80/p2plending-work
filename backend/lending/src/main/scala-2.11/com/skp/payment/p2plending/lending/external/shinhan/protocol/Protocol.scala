package com.skp.payment.p2plending.lending.external.shinhan.protocol

import com.skp.payment.p2plending.lending.external.shinhan.protocol.FieldFormat.FieldFormat

import scala.reflect.runtime.universe._

/**
 * 신한은행 필드 type
 */
object FieldFormat {
  sealed trait FieldFormat

  case object CHAR extends FieldFormat
  case object NUM extends FieldFormat //숫자는 무조건 long이다.
}

/**
 * 신한은행 전문 포맷
 * @param fieldName : 필드이름
 * @param format    : data type. FieldFormat 참고
 * @param len       : 필드길이
 * @param fixedValue  : 고정값, 없으면 입력하지 않음.
 */
case class ShinHanField(fieldName: String, format: FieldFormat, len: Int, fixedValue: Option[String] = None) {
  def encode[T](t: T): Array[Byte] = {
    //고정값 여부
    val value = fixedValue match {
      case None => t
      case Some(x) => x
    }

    //String으로 변경함.
    val strValue = value match {
      case str: String => str
      case int: Int => int.toString
      case long: Long => long.toString
    }

    val bytes = strValue.getBytes("UTF-8")

    //format에 따라 left-justified(blank fill), right-justifed('0' fill) 적용
    val result = format match {
      case FieldFormat.CHAR => {
        if (bytes.length < len)
          bytes ++ "".padTo(len - bytes.length, " ").mkString.getBytes("UTF-8")
        else
          bytes.take(len)
      }

      case FieldFormat.NUM => {
        if (bytes.length < len)
          "".padTo(len - bytes.length, "0").mkString.getBytes("UTF-8") ++ bytes
        else
          bytes.take(len)
      }
    }

    return result
  }

  def decode(bytes: Array[Byte], offset: Int): Any = {
    val offsetStart = bytes.drop(offset)
    val dataBytes = offsetStart.take(len)

    decode(dataBytes)
  }

  def decode(bytes: Array[Byte]): Any = {
    if (bytes.length != len)
      throw new IllegalArgumentException("decoding byte length is not valid")

    //고정값 여부
    val value = fixedValue match {
      case None => new String(bytes, "UTF-8")
      case Some(x) => new String(x)
    }

    val result = format match {
      case FieldFormat.CHAR => value.trim
      case FieldFormat.NUM => value.toLong
    }

    return result
  }
}

/**
 * 신한은행 전문 case class를 이 클래스를 상속받아야 함.
 * implicit 필요: ex) implicit def getType: Type = typeOf[AccountNameSelectRequest]
 */
trait ShinHanRequestProtocolHandler {

  import scala.reflect.runtime.{universe => ru}
  def getBytes(shinHanFields: List[ShinHanField])(implicit tpe: Type): Array[Byte] = {
    val classLoaderMirror = ru.runtimeMirror(this.getClass.getClassLoader)
    val im = classLoaderMirror.reflect(this)

    shinHanFields.toStream
      .map(shinHanField => {
      val decl = tpe.decl(ru.TermName(shinHanField.fieldName))
      if (decl.fullName == "<none>")
        throw new IllegalArgumentException(s"No declaration field: ${shinHanField.fieldName}")

      val termSymbol = decl.asTerm
      val fieldMirror = im.reflectField(termSymbol)
      (shinHanField, Option(fieldMirror.get).getOrElse(""))
    }).map(fieldAndValue => fieldAndValue._1.encode(fieldAndValue._2))
      .reduce(_ ++ _)
  }
}

object ShinHanResponseProtocolHandler extends ShinHanResponseProtocolHandler
trait ShinHanResponseProtocolHandler {
  import scala.reflect.runtime.{universe => ru}

  class ClassFactory[T: TypeTag] {
    val tpe = typeOf[T]
    val classSymbol = tpe.typeSymbol.asClass

    if (!(tpe <:< typeOf[Product] && classSymbol.isCaseClass))
      throw new IllegalArgumentException("only applies to case classes")

    val classLoaderMirror = ru.runtimeMirror(this.getClass.getClassLoader)
    val classMirror = classLoaderMirror.reflectClass(classSymbol)

    val constructorSymbol = tpe.decl(termNames.CONSTRUCTOR)

    val defaultConstructor =
      if (constructorSymbol.isMethod) constructorSymbol.asMethod
      else {
        val ctors = constructorSymbol.asTerm.alternatives
        ctors.map { _.asMethod }.find { _.isPrimaryConstructor }.get
      }

    val constructorMethod = classMirror.reflectConstructor(defaultConstructor)

    def buildWith(bytes: Array[Byte], shinHanFields: List[ShinHanField]): T = {
      var offset = 0
      val dataSeq: Seq[Any] = shinHanFields.map(shinHanField => {
        val data = shinHanField.decode(bytes, offset)
        offset += shinHanField.len
        data
      })

      constructorMethod(dataSeq: _*).asInstanceOf[T]
    }
  }
}
