package com.github.horitaku1124.kotlin.sql_minus

import com.github.horitaku1124.kotlin.sql_minus.dialect_o.Column
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.Record

interface TableMapper : AutoCloseable {
  fun insert(columns: List<String>, record: Record)
  fun columns(): List<Column>
  fun select(columns: List<String>): List<Record>
  fun update(record: Record)
  fun delete(record: Record)
  fun createTable()
}