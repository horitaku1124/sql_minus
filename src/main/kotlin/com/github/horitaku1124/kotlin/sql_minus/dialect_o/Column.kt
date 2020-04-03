package com.github.horitaku1124.kotlin.sql_minus.dialect_o

class Column {
  lateinit var name:String
  lateinit var type:Type

  enum class Type {
    INT, VARCHAR
  }
}