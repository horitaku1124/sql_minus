package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.Recipe
import java.util.*

class QueryRecipe(var type: QueryType) {
  lateinit var subject: String
  var recipe: Optional<Recipe> = Optional.empty()

  override fun toString(): String {
    return "$type -> $subject"
  }
}