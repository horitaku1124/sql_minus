package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.ColumnType
import com.github.horitaku1124.kotlin.sql_minus.ColumnType.*

class RecordCell(var type: ColumnType, value: String) {
  var isNull = true
  var intValue: Int? = null
  var textValue: String? = null
  init {
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