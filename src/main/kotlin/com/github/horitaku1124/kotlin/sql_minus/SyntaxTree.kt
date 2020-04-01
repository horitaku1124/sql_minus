package com.github.horitaku1124.kotlin.sql_minus

class SyntaxTree(var type: QueryType) {
  var subject = arrayListOf<String>()

  override fun toString(): String {
    return "${type} -> ${subject}"
  }
}