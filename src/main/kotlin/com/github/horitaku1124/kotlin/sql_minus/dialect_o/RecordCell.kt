package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.ColumnType
import com.github.horitaku1124.kotlin.sql_minus.ColumnType.*

class RecordCell(var type: ColumnType, value: String) {
  var intValue: Int? = null
  var textValue: String? = null
  init {
    if (type == INT) {
      intValue = value.toInt()
    } else if (type == SMALLINT) {
      intValue = value.toShort().toInt()
    } else if (type == VARCHAR) {
      textValue = value
    }
  }
}