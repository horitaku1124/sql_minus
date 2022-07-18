package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.Recipe
import java.util.*

class QueryRecipe(var type: QueryType) {
  var subject: String = "NULL"
  var recipe: Optional<Recipe> = Optional.empty()

  override fun toString(): String {
    return "$type -> $subject"
  }
}