package com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes

import com.github.horitaku1124.kotlin.sql_minus.Recipe

class UpdateQueryRecipe: Recipe() {
  var targetTable: String? = null
  var updates = arrayListOf<UpdatesRecipe>()
  var whereTree = arrayListOf<WhereRecipes>()
}