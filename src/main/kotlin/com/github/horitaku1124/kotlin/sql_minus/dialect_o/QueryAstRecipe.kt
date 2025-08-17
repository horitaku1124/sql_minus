package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.Recipe
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.ast.Node
import java.util.*

class QueryAstRecipe(var type: QueryType) {
  var recipe: Optional<Recipe> = Optional.empty()
  var syntaxTree: Optional<Node> = Optional.empty()

  override fun toString(): String {
    return "$type -> Recipe=$recipe syntaxTree=$syntaxTree"
  }
}
