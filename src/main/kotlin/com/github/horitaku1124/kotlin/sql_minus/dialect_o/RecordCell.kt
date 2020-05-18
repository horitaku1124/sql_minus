package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.ColumnType
import com.github.horitaku1124.kotlin.sql_minus.ColumnType.*
import java.math.BigDecimal

class RecordCell{
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
  constructor(type: ColumnType, value: String) {
    if (type == INT) {
      intValue = value.toInt()
      isNull = intValue == null
    } else if (type == SMALLINT) {
      intValue = value.toShort().toInt()
      isNull = intValue == null
    } else if (type == VARCHAR) {
      textValue = value
      isNull = textValue == null
    }
  }
}