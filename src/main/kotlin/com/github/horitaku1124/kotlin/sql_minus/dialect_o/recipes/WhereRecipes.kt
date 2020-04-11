package com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes

import java.util.*

class WhereRecipes {
  var expression = arrayListOf<String>()
  var andWhere = Optional.empty<WhereRecipes>()
  var orWhere = Optional.empty<WhereRecipes>()
}