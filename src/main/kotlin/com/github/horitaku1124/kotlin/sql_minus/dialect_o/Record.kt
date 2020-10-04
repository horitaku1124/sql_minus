package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.DataRecord
import com.github.horitaku1124.kotlin.sql_minus.IRecord

class Record: DataRecord(), IRecord {
  var position: Long? = null
  var cells = arrayListOf<RecordCell>()
}