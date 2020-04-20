package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.WhereRecipes
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.WhereVerifyGate
import java.util.function.Predicate

class QueryCompiler {
  fun compileWhere(columns: List<Column>, recipe: WhereRecipes): WhereVerifyGate {
    if (recipe.expression.size == 3) {
      val colName = recipe.expression[0]
      var colIndex = -1
      for (i in 0 until columns.size) {
        if (columns[i].name == colName) {
          colIndex = i
          break
        }
      }
      return WhereVerifyGate.andRule(colIndex, recipe.expression[1], recipe.expression[2])
    } else {
      return WhereVerifyGate(Predicate<Record> {
        true
      })
    }
  }
}