package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.dialect_o.QueryType.*
import com.github.horitaku1124.kotlin.sql_minus.SyntaxTree
import java.lang.RuntimeException
import java.util.*

class Tokenizer {
  fun parse(tokens: List<String>):List<SyntaxTree> {
    val syntaxTrees = arrayListOf<SyntaxTree>()

    var index = 0
    while (index < tokens.size) {
      var startToken = tokens[index++].toLowerCase()
      var syntax: SyntaxTree
      if (startToken == "create") {
        val objective = tokens[index++].toLowerCase()
        if (objective == "database") {
          syntax = SyntaxTree(CREATE_DATABASE)
          val subject = tokens[index++]
          syntax.subject = subject
        } else if (objective == "table") {
          val ret = parseCreateTable(tokens, index)
          syntax = ret.first
          index = ret.second
        } else {
          throw RuntimeException("unrecognized command => $objective")
        }
      } else if (startToken == "connect") {
        syntax = SyntaxTree(CHANGE_DATABASE)
        var subject = tokens[index++].toLowerCase()
        syntax.subject = subject
      } else  {
        throw RuntimeException("unrecognized command => $startToken")
      }
      while (index < tokens.size) {
        val token = tokens[index++]
        if (token == ";") {
          break
        }
      }
      syntaxTrees.add(syntax)
    }
    return syntaxTrees
  }

  private fun parseCreateTable(tokens: List<String>, startIndex: Int): Pair<SyntaxTree, Int> {
    val syntax = SyntaxTree(CREATE_TABLE)
    var index = startIndex
    syntax.subject = tokens[index++]
    val parenthesis = tokens[index++]
    if (parenthesis != "(") {
      throw RuntimeException("error")
    }
    val recipe = CreateTableRecipe()
    while (index < tokens.size) {
      val columnParts = arrayListOf<String>()
      while (index < tokens.size) {
        val part = tokens[index++]
        if (part == ",") {
          break
        }
        if (part == ")") {
          break
        }
        columnParts.add(part)
      }
      val col = Column()
      col.name = columnParts[0]
      val type = columnParts[1].toLowerCase()
      if (type == "int") {
        col.type = Column.Type.INT
      }
      recipe.columns.add(col)
    }
    syntax.recipe = Optional.of(recipe)
    return Pair(syntax, index)
  }
}