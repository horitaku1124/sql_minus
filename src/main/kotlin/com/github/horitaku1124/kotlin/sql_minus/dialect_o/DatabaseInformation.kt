package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.dialect_o.journals.Table

class DatabaseInformation: java.io.Serializable {
  var tables = arrayListOf<Table>()
}