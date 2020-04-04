package com.github.horitaku1124.kotlin.sql_minus.dialect_o.journals

import com.github.horitaku1124.kotlin.sql_minus.dialect_o.Column

class TableJournal(var name: String): java.io.Serializable {
  var columns: ArrayList<Column> = arrayListOf()
  lateinit var fileName: String
}