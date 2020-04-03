package com.github.horitaku1124.kotlin.sql_minus

import com.github.horitaku1124.kotlin.sql_minus.dialect_o.QueryType
import java.util.*

class SyntaxTree(var type: QueryType) {
  lateinit var subject: String
  var recipe: Optional<Recipe> = Optional.empty()

  override fun toString(): String {
    return "${type} -> ${subject}"
  }
}