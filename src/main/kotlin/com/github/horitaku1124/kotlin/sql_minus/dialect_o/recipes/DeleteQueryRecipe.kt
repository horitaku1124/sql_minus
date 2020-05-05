package com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes

import com.github.horitaku1124.kotlin.sql_minus.Recipe

class DeleteQueryRecipe: Recipe() {
  var targetTable: String? = null
  var whereTree = arrayListOf<WhereRecipes>()
}