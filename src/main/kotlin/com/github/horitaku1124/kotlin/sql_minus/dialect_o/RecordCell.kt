package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.ColumnType
import com.github.horitaku1124.kotlin.sql_minus.ColumnType.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

class RecordCell{
  companion object {
    private val dateFormat = DateTimeFormatter
      .ofPattern("uuuu-MM-dd")
      .withResolverStyle(ResolverStyle.STRICT)
    private val timestampFormat = DateTimeFormatter
      .ofPattern("uuuu-MM-dd HH:mm:ss")
      .withResolverStyle(ResolverStyle.STRICT)
  }

  var type: ColumnType = NULL
  var isNull = true
  var intValue: Int? = null
  var textValue: String? = null
  var numberValue: BigDecimal? = null

  constructor(number: BigDecimal) {
    type = NUMBER
    numberValue = number
    isNull = false
  }
  constructor(type: ColumnType) {
    this.type = type
    isNull = true
  }
  constructor(type: ColumnType, value: String) {
    this.type = type
    when (type) {
      INT -> {
        intValue = value.toInt()
        isNull = intValue == null
      }
      SMALLINT -> {
        intValue = value.toShort().toInt()
        isNull = intValue == null
      }
      VARCHAR,CHAR -> {
        textValue = value
        isNull = textValue == null
      }
      TIMESTAMP -> {
        val date = LocalDateTime.parse(value, timestampFormat)
        intValue = date.toEpochSecond(ZoneOffset.UTC).toInt()
        isNull = false
      }
      DATE -> {
        val date = LocalDate.parse(value, dateFormat)
        intValue = date.toEpochDay().toInt()
        isNull = false
      }
      NUMBER -> TODO()
      else -> {
        isNull = true
      }
    }
  }

  fun getDateValue(): LocalDate {
    return LocalDate.ofEpochDay(intValue!!.toLong())
  }
  fun getTimeStampValue(): LocalDateTime {
    return LocalDateTime.ofEpochSecond(intValue!!.toLong(), 0, ZoneOffset.UTC)
  }
}