package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.ColumnType
import java.util.*

class Column: java.io.Serializable {
  lateinit var name: String
  lateinit var type: ColumnType
//  var length: Optional<Int> = Optional.empty()
  var length: Int? = null
}