package com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes

import com.github.horitaku1124.kotlin.sql_minus.Recipe
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.Column

class CreateTableRecipe(var name: String): Recipe() {
  var columns: ArrayList<Column> = arrayListOf()
}