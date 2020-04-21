package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.ColumnType
import com.github.horitaku1124.kotlin.sql_minus.DBRuntimeException
import com.github.horitaku1124.kotlin.sql_minus.SyntaxTree
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.QueryType.*
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.*
import java.util.*

class Tokenizer {
  fun parse(tokens: List<String>):List<SyntaxTree> {
    val syntaxTrees = arrayListOf<SyntaxTree>()

    var index = 0
    while (index < tokens.size) {
      val startToken = tokens[index++].toLowerCase()
      var syntax: SyntaxTree
      when (startToken) {
        "create" -> {
          when (val objective = tokens[index++].toLowerCase()) {
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
          if (objective != "tables") {
            throw DBRuntimeException("unrecognized command => $objective")
          }
          syntax = SyntaxTree(SHOW_TABLES)
        }
        "insert" -> {
          val objective = tokens[index++].toLowerCase()
          if (objective != "into") {
            throw DBRuntimeException("unrecognized command => $objective")
          }
          val ret = parseInsertTable(tokens, index)
          syntax = ret.first
          index = ret.second
        }
        "select" -> {
          val ret = parseSelect(tokens, index)
          syntax = ret.first
          index = ret.second
        }
        "drop" -> {
          val objective = tokens[index++].toLowerCase()
          if (objective == "table") {
            syntax = SyntaxTree(DROP_TABLE)
            val subject = tokens[index++]
            syntax.subject = subject
          } else {
            throw DBRuntimeException("unrecognized command => $objective")
          }
        }
        "update" -> {
          val ret = parseUpdate(tokens, index)
          syntax = ret.first
          index = ret.second
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
      var partsInParen = arrayListOf<String>()
      var insideParenthesis = false
      while (index < tokens.size) {
        val part = tokens[index++]
        if (insideParenthesis) {
          if (part == ")") {
            insideParenthesis = false
          } else if (part == ",") {
            continue
          } else {
            partsInParen.add(part)
          }
        } else {
          if (part == ")") {
            break
          }
          if (part == ",") {
            break
          }
          if (part == "(") {
            insideParenthesis = true
            continue
          }
        }
        columnParts.add(part)
      }
      val col = Column()
      col.name = columnParts[0]
      val type = columnParts[1].toLowerCase()
      if (type == "int") {
        col.type = ColumnType.INT
      } else if (type == "varchar") {
        col.type = ColumnType.VARCHAR
        if (partsInParen.isEmpty()) {
          throw DBRuntimeException("inside parenthesis must not empty")
        }
        if (partsInParen.size == 1) {
          col.length = partsInParen[0].toInt()
        }
      } else if (type == "smallint") {
        col.type = ColumnType.SMALLINT
      }
      recipe.columns.add(col)
    }
    syntax.recipe = Optional.of(recipe)
    return Pair(syntax, index)
  }

  private fun parseInsertTable(tokens: List<String>, startIndex: Int): Pair<SyntaxTree, Int> {
    var index = startIndex
    val syntax = SyntaxTree(INSERT_QUERY)
    syntax.subject = tokens[index++]
    if (tokens[index++] != "(") {
      throw DBRuntimeException("error")
    }
    val columns = arrayListOf<String>()

    while (index < tokens.size) {
      val token = tokens[index++]
      if (token == ")") break
      if (token == ",") continue
      columns.add(token)
    }
    if (tokens[index++] != "values") {
      throw DBRuntimeException("error")
    }

    val records = arrayListOf<Record>()
    while (index < tokens.size) {
      val token = tokens[index++]
      if (token == ",") continue
      if (token != "(") break

      val record = Record()
      while (index < tokens.size) {
        val value = tokens[index++]
        if (value == ")") break
        if (value == ",") continue

        if (value.startsWith("'")) {
          record.cells.add(RecordCell(ColumnType.VARCHAR, value.substring(1, value.length - 1)))
        } else {
          record.cells.add(RecordCell(ColumnType.INT, value))
        }
      }

      records.add(record)
    }
    val recipe = InsertIntoRecipe(syntax.subject)
    recipe.columns = columns
    recipe.records = records
    syntax.recipe = Optional.of(recipe)

    return Pair(syntax, index)
  }

  private fun parseSelect(tokens: List<String>, startIndex: Int): Pair<SyntaxTree, Int>  {
    val syntax = SyntaxTree(SELECT_QUERY)
    var index = startIndex
    var selectParts = arrayListOf<String>()
    var token = tokens[index++]
    var nextToken: String? = null
    while (index < tokens.size) {
      selectParts.add(token)
      val next = tokens[index++]
      if (next == ",") {
        token = tokens[index++]
        continue
      }
      nextToken = next
      break
    }
    if (nextToken == null || nextToken.toLowerCase() != "from") {
      throw DBRuntimeException("no from syntax")
    }
    val fromParts = arrayListOf<String>()
    fromParts.add(tokens[index++])

    val selectRecipe = SelectQueryRecipe()
    while (index < tokens.size) {
      var remainNum = tokens.size - index
      if (remainNum > 0) {
        nextToken = tokens[index++]
        if (nextToken.toLowerCase() == "where") {
          remainNum--
          while(remainNum > 0) {
            if (remainNum > 2) {
              var subject = tokens[index++]
              var operator = tokens[index++]
              var objective = tokens[index++]
              remainNum = remainNum - 3
              selectRecipe.whereTree.expression.add(subject)
              selectRecipe.whereTree.expression.add(operator)
              selectRecipe.whereTree.expression.add(objective)

              if (remainNum > 0) {
                nextToken = tokens[index++].toLowerCase()
                remainNum--
                if (nextToken == "and" || nextToken == "or") {
                  selectRecipe.whereTree.expression.add(nextToken)
                }
              }
            }
          }
        }
      } else {
        break
      }
    }

    selectRecipe.selectParts = selectParts
    selectRecipe.fromParts = fromParts

    syntax.recipe = Optional.of(selectRecipe)

    return Pair(syntax, index)
  }

  private fun parseUpdate(tokens: List<String>, startIndex: Int): Pair<SyntaxTree, Int> {
    var recipe = UpdateQueryRecipe()
    var index = startIndex
    val syntax = SyntaxTree(UPDATE_QUERY)
    recipe.targetTable = tokens[index++]

    if (tokens[index++] != "set") {
      throw DBRuntimeException("no set syntax")
    }

    while (index < tokens.size) {
      var token = tokens[index++]
      if (token == ",") {
        continue
      }
      if (token == "where") {
        break
      }
      var subject = token
      var ope = tokens[index++]
      var objective = tokens[index++]
      var upd = UpdatesRecipe()
      upd.expression.add(subject)
      upd.expression.add(ope)
      upd.expression.add(objective)

      recipe.updates.add(upd)
    }

    while (index < tokens.size) {
      var subject = tokens[index++]
      var ope = tokens[index++]
      var objective = tokens[index++]

      var where = WhereRecipes()
      where.expression.add(subject)
      where.expression.add(ope)
      where.expression.add(objective)

      recipe.whereTree.add(where)
    }

    syntax.recipe = Optional.of(recipe)
    return Pair(syntax, index)
  }
}