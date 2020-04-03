package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.Recipe

class CreateTableRecipe: Recipe() {
  var columns: ArrayList<Column> = arrayListOf()
}