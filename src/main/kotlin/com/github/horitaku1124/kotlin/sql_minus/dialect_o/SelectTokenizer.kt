package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.ast.Node
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.ast.NodeType
import java.util.*

class SelectTokenizer {
  fun parseSelect(tokens: List<String>, startIndex: Int): Pair<QueryAstRecipe, Int> {
    val result = QueryAstRecipe(QueryType.SELECT_QUERY)
    val (node, index) = parseLayer1(tokens, startIndex)

    result.syntaxTree = Optional.of(node)

    return Pair(result, index)
  }

  enum class WhereAmI {
    SELECT, FROM, WHERE, NONE
  }

  private val reservedName = listOf("select", "from", "where")
  private val compareOperators = listOf("=", ">", "<", "in", "<>")

  private fun parseLayer1(tokens: List<String>, startIndex: Int): Pair<Node, Int> {
    val parentNode = Node(NodeType.MAP)
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
          parentNode.property["select"] = children
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
          children.add(Node(NodeType.VALUE, value = subject))
          parentNode.property["from"] = children
          children = arrayListOf()
          amI = WhereAmI.NONE
        } else if (subject == ",") {
          continue
        } else {
          children.add(Node(NodeType.VALUE, value = subject))
        }
        continue
      } else if (amI == WhereAmI.WHERE) {
        val subject = token
        val next1 = if (tokens.size > index) tokens[index] else ""
        val next2 = if (tokens.size > index + 1) tokens[index + 1] else ""
        if (compareOperators.contains(next1)) {
          val node1 = Node(
            NodeType.LIST, children = arrayListOf(
              Node(NodeType.VALUE, value = subject),
              Node(NodeType.VALUE, value = next1),
              Node(NodeType.VALUE, value = next2),
            )
          )
          children.add(node1)
        }
        continue
      }

      when (token.toLowerCase()) {
        "select" -> {
          amI = WhereAmI.SELECT
          children.add(Node(NodeType.VALUE, value = tokens[index++]))
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
      parentNode.property["select"] = children
    } else if (amI == WhereAmI.FROM) {
      parentNode.property["from"] = children
    } else if (amI == WhereAmI.WHERE) {
      parentNode.property["where"] = children
    }

    return Pair(parentNode, index)
  }
}