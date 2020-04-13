package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.dialect_o.journals.TableJournal

class DatabaseInformation: java.io.Serializable {
  var name: String? = null
  var tables = arrayListOf<TableJournal>()
}