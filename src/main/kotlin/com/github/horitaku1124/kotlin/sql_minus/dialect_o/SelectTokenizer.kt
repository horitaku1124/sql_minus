package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.ast.Node
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.ast.NodeType
import java.util.*

class SelectTokenizer {
  fun parseSelect(tokens: List<String>, startIndex: Int): Pair<QueryAstRecipe, Int>  {
    val result = QueryAstRecipe(QueryType.SELECT_QUERY)
    val (node, index) = parseLayer1(tokens, startIndex)

    result.syntaxTree = Optional.of(node)

    return Pair(result, index)
  }

  enum class WhereAmI {
    SELECT, FROM, WHERE, NONE
  }

  private val reservedName = listOf("select", "from", "where")

  private fun parseLayer1(tokens: List<String>, startIndex: Int): Pair<Node, Int> {
    var node = Node(NodeType.MAP)
    var index = startIndex

    var amI = WhereAmI.NONE
    var children = arrayListOf<Node>()
    while (index < tokens.size) {
      val token = tokens[index++]

      if (amI == WhereAmI.SELECT) {
        if (token == ",") {
          children.add(Node(NodeType.VALUE, value = tokens[index++]))
          continue
        } else {
          node.property["select"] = children
          children = arrayListOf()
          amI = WhereAmI.NONE
        }
      } else if (amI == WhereAmI.FROM) {
        val subject = token
        val nextOfSubject = if (tokens.size > index) tokens[index] else ""
        if (nextOfSubject == ",") {
          children.add(Node(NodeType.VALUE, value = subject))
          index++
        } else if (reservedName.contains(nextOfSubject.toLowerCase())) {
          node.property["from"] = children
          children = arrayListOf()
          amI = WhereAmI.NONE
        } else if (subject == ",") {
          continue
        } else {
          children.add(Node(NodeType.VALUE, value = subject))
        }
        continue
      }

      when (token.toLowerCase()) {
        "select" -> {
          amI = WhereAmI.SELECT
          children.add(Node(NodeType.VALUE, value = tokens[index++]) )
        }
        "from" -> {
          amI = WhereAmI.FROM
        }
        "where" -> {
          amI = WhereAmI.WHERE
        }
      }
    }
    if (amI == WhereAmI.SELECT) {
      node.property["select"] = children
    } else if (amI == WhereAmI.FROM) {
      node.property["from"] = children
    }

    return Pair(node, index)
  }
}