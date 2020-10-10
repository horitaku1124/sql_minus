package com.github.horitaku1124.kotlin.sql_minus

interface TableMapper : AutoCloseable {
  fun insert(columns: List<String>, record: IRecord)
  fun columns(): List<IColumn>
  // TODO not need to take 'columns'
  fun select(columns: List<String>): List<IRecord>
  fun update(record: IRecord)
  fun delete(record: IRecord)
  fun createTable()
}