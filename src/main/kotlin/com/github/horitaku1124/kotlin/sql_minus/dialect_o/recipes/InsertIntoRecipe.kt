package com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes

import com.github.horitaku1124.kotlin.sql_minus.Recipe
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.Record

class InsertIntoRecipe(var name: String): Recipe() {
  var columns = arrayListOf<String>()
  var records = arrayListOf<Record>()
}