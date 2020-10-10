package com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes

import com.github.horitaku1124.kotlin.sql_minus.Recipe

@Deprecated("migrate to SelectInvocationRecipe")
class SelectQueryRecipe: Recipe() {
  var selectParts = arrayListOf<String>()
  var fromParts = arrayListOf<String>()
  var whereTree = WhereRecipes()
}