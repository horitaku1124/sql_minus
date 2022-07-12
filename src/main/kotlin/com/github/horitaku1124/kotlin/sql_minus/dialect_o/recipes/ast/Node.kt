package com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.ast

data class Node(
  var type: NodeType,
  var children: List<Node> = arrayListOf(),
  var property: HashMap<String, List<Node>> = hashMapOf(),
  var value: String? = null
)
