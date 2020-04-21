package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.DBRuntimeException
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.WhereRecipes
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.WhereVerifyGate
import java.util.function.Predicate

class QueryCompiler {
  fun compileWhere(columns: List<Column>, recipe: WhereRecipes): WhereVerifyGate {
    if (recipe.expression.size == 0) {
      return WhereVerifyGate(Predicate<Record> {
        true
      })
    }

    var compiled:WhereVerifyGate? = null

    var index = 0
    var isAnd = true
    while (index < recipe.expression.size) {
      val colName = recipe.expression[index]
      if (colName.toLowerCase() == "and") {
        isAnd = true
        index++
      } else if ((recipe.expression.size - index) >= 3) {
        var colIndex = -1
        for (i in 0 until columns.size) {
          if (columns[i].name == colName) {
            colIndex = i
            break
          }
        }
        var compiled2:WhereVerifyGate
        compiled2 = WhereVerifyGate.addOperand(colIndex,
          recipe.expression[index + 1],
          recipe.expression[index + 2])
        if (compiled == null) {
          compiled = compiled2
        } else if (isAnd) {
          compiled.andRules.add(compiled2)
        }
        index += 3
      }
    }

    return compiled ?: throw DBRuntimeException("error")
  }
}