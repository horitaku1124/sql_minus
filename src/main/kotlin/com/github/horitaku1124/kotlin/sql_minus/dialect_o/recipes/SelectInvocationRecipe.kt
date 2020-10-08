package com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes

import com.github.horitaku1124.kotlin.sql_minus.Recipe
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.SelectInvocationRecipe.InvocationTask.TaskType.*

class SelectInvocationRecipe: Recipe() {
  val tasks = arrayListOf<InvocationTask>()


  open class InvocationTask(var type: TaskType) {
    enum class TaskType {
      TableRead,
      Selection,
      Filtering,
    }

  }
  class TableReadTask(var tableName: String,
                      var alias: String = tableName): InvocationTask(TableRead) {

  }

  class SelectionTask(var columns: List<String>): InvocationTask(Selection) {

  }

  class FilteringTask(var wheres: WhereRecipes): InvocationTask(Filtering) {

  }
}