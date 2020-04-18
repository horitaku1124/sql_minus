package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.DataRecord

class Record: DataRecord() {
  var position: Long? = null
  var cells = arrayListOf<RecordCell>()
}