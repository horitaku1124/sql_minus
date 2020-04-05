package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.DBRuntimeException
import com.github.horitaku1124.kotlin.sql_minus.SyntaxTree
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.QueryType.*
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.CreateTableRecipe
import java.util.*

class Tokenizer {
  fun parse(tokens: List<String>):List<SyntaxTree> {
    val syntaxTrees = arrayListOf<SyntaxTree>()

    var index = 0
    while (index < tokens.size) {
      var startToken = tokens[index++].toLowerCase()
      var syntax: SyntaxTree
      when (startToken) {
        "create" -> {
          val objective = tokens[index++].toLowerCase()
          when (objective) {
            "database" -> {
              syntax = SyntaxTree(CREATE_DATABASE)
              val subject = tokens[index++]
              syntax.subject = subject
            }
            "table" -> {
              val ret = parseCreateTable(tokens, index)
              syntax = ret.first
              index = ret.second
            }
            else -> {
              throw DBRuntimeException("unrecognized command => $objective")
            }
          }
        }
        "connect" -> {
          syntax = SyntaxTree(CHANGE_DATABASE)
          syntax.subject = tokens[index++].toLowerCase()
        }
        "show" -> {
          val objective = tokens[index++].toLowerCase()
          if (objective == "tables") {
            syntax = SyntaxTree(SHOW_TABLES)
          } else {
            throw DBRuntimeException("unrecognized command => $objective")
          }
        }
        else -> {
          throw DBRuntimeException("unrecognized command => $startToken")
        }
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
      throw DBRuntimeException("error")
    }
    val recipe = CreateTableRecipe(syntax.subject)
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