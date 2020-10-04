package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.ColumnType
import com.github.horitaku1124.kotlin.sql_minus.IColumn

class Column: java.io.Serializable, IColumn {
  lateinit var name: String
  lateinit var type: ColumnType
  var length: Int? = null
  var numberFormat: Pair<Int, Int>? = null

  constructor()
  constructor(name: String, type: ColumnType) {
    this.name = name
    this.type = type
  }
}