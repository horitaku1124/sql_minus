package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.ColumnType
import com.github.horitaku1124.kotlin.sql_minus.DBRuntimeException
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.QueryType.*
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.*
import java.util.*

class Tokenizer {
  private val typeAnnotations = listOf("timestamp", "date")

  fun parse(tokens: List<String>):List<QueryRecipe> {
    val syntaxTrees = arrayListOf<QueryRecipe>()

    var index = 0
    while (index < tokens.size) {
      val startToken = tokens[index++].lowercase(Locale.getDefault())
      var syntax: QueryRecipe
      when (startToken) {
        "create" -> {
          when (val objective = tokens[index++].lowercase(Locale.getDefault())) {
            "database" -> {
              syntax = QueryRecipe(CREATE_DATABASE)
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
          syntax = QueryRecipe(CHANGE_DATABASE)
          syntax.subject = tokens[index++].lowercase(Locale.getDefault())
        }
        "show" -> {
          val objective = tokens[index++].lowercase(Locale.getDefault())
          if (objective != "tables") {
            throw DBRuntimeException("unrecognized command => $objective")
          }
          syntax = QueryRecipe(SHOW_TABLES)
        }
        "insert" -> {
          val objective = tokens[index++].lowercase(Locale.getDefault())
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
          val objective = tokens[index++].lowercase(Locale.getDefault())
          if (objective == "table") {
            syntax = QueryRecipe(DROP_TABLE)
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
        "delete" -> {
          val ret = parseDelete(tokens, index)
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

  private fun parseCreateTable(tokens: List<String>, startIndex: Int): Pair<QueryRecipe, Int> {
    val syntax = QueryRecipe(CREATE_TABLE)
    var index = startIndex
    syntax.subject = tokens[index++]
    val parenthesis = tokens[index++]
    if (parenthesis != "(") {
      throw DBRuntimeException("error")
    }
    val recipe = CreateTableRecipe(syntax.subject)
    while (index < tokens.size) {
      val columnParts = arrayListOf<String>()
      val partsInParen = arrayListOf<String>()
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
      val type = columnParts[1].lowercase(Locale.getDefault())
      when (type) {
        "int" -> {
          col.type = ColumnType.INT
        }
        "varchar" -> {
          col.type = ColumnType.VARCHAR
          if (partsInParen.isEmpty()) {
            throw DBRuntimeException("inside parenthesis must not empty")
          }
          if (partsInParen.size == 1) {
            col.length = partsInParen[0].toInt()

            if (col.length!! > 2000) {
              throw DBRuntimeException("VARCHAR(SIZE) must less than 32,767")
            }
          }
        }
        "varchar2" -> {
          col.type = ColumnType.VARCHAR
          if (partsInParen.isEmpty()) {
            throw DBRuntimeException("inside parenthesis must not empty")
          }
          if (partsInParen.size == 1) {
            col.length = partsInParen[0].toInt()
          }
        }
        "char" -> {
          col.type = ColumnType.CHAR
          if (partsInParen.size == 1) {
            col.length = partsInParen[0].toInt()
            if (col.length!! > 2000) {
              throw DBRuntimeException("CHAR(SIZE) must less than 2000")
            }
          } else if (partsInParen.size == 0) {
            col.length = 1
          }
        }
        "smallint" -> {
          col.type = ColumnType.SMALLINT
        }
        "number" -> {
          col.type = ColumnType.NUMBER
          if (partsInParen.size == 1) {
            col.numberFormat = Pair(partsInParen[0].toInt(), 0)
          } else if (partsInParen.size == 2) {
            col.numberFormat = Pair(partsInParen[0].toInt(), partsInParen[1].toInt())
          }
        }
        "timestamp" -> {
          col.type = ColumnType.TIMESTAMP
        }
        "date" -> {
          col.type = ColumnType.DATE
        }
      }
      recipe.columns.add(col)
    }
    syntax.recipe = Optional.of(recipe)
    return Pair(syntax, index)
  }

  private fun parseInsertTable(tokens: List<String>, startIndex: Int): Pair<QueryRecipe, Int> {
    var index = startIndex
    val syntax = QueryRecipe(INSERT_QUERY)
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
      throw DBRuntimeException("unrecognized token:" + tokens[index])
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
        } else if (typeAnnotations.contains(value.lowercase(Locale.getDefault()))) {
          val annotation = value.lowercase(Locale.getDefault())
          val value2 = tokens[index++]
          if (value2.startsWith("'")) {
            val strValue = value2.substring(1, value2.length - 1)
            if (annotation == "timestamp") {
              record.cells.add(RecordCell(ColumnType.TIMESTAMP, strValue))
            } else if (annotation == "date") {
              record.cells.add(RecordCell(ColumnType.DATE, strValue))
            }
          } else {
            throw DBRuntimeException("annotated value parse error")
          }
        } else if (value.contains('.')) {
          record.cells.add(RecordCell(ColumnType.VARCHAR, value))
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

  fun parseSelect(tokens: List<String>, startIndex: Int): Pair<QueryRecipe, Int>  {
    val syntax = QueryRecipe(SELECT_QUERY)
    val recipe = SelectInvocationRecipe()

    var index = startIndex
    val selectParts = arrayListOf<String>()
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
    if (nextToken == null || nextToken.lowercase(Locale.getDefault()) != "from") {
      throw DBRuntimeException("no from syntax")
    }
    val fromParts = arrayListOf<String>()
    fromParts.add(tokens[index++])


    val whereTree = WhereRecipes()
    while (index < tokens.size) {
      var remainNum = tokens.size - index
      if (remainNum > 0) {
        nextToken = tokens[index++]
        if (nextToken.lowercase(Locale.getDefault()) == "where") {
          remainNum--
          while (remainNum > 0) {
            if (remainNum > 2) {
              val subject = tokens[index++]
              val operator = tokens[index++]
              val objective = tokens[index++]
              remainNum = remainNum - 3
              whereTree.expression.add(subject)
              whereTree.expression.add(operator)
              whereTree.expression.add(objective)

              if (remainNum > 0) {
                nextToken = tokens[index++].lowercase(Locale.getDefault())
                remainNum--
                if (nextToken == "and" || nextToken == "or") {
                  whereTree.expression.add(nextToken)
                }
              }
            }
          }
        }
      } else {
        break
      }
    }


    // Enclosing
    for (from in fromParts) {
      recipe.tasks.add(SelectInvocationRecipe.TableReadTask(from))
    }
    if (whereTree.expression.size > 0) {
      recipe.tasks.add(SelectInvocationRecipe.FilteringTask(whereTree))
    }
    recipe.tasks.add(SelectInvocationRecipe.SelectionTask(selectParts))

    syntax.recipe = Optional.of(recipe)
    return Pair(syntax, index)
  }

  private fun parseUpdate(tokens: List<String>, startIndex: Int): Pair<QueryRecipe, Int> {
    val recipe = UpdateQueryRecipe()
    var index = startIndex
    val syntax = QueryRecipe(UPDATE_QUERY)
    recipe.targetTable = tokens[index++]

    if (tokens[index++] != "set") {
      throw DBRuntimeException("no set syntax")
    }

    while (index < tokens.size) {
      val token = tokens[index++]
      if (token == ",") {
        continue
      }
      if (token == "where") {
        break
      }
      val subject = token
      val ope = tokens[index++]
      val objective = tokens[index++]
      val upd = UpdatesRecipe()
      upd.expression.add(subject)
      upd.expression.add(ope)
      upd.expression.add(objective)

      recipe.updates.add(upd)
    }

    while (index < tokens.size) {
      val subject = tokens[index++]
      val ope = tokens[index++]
      val objective = tokens[index++]

      val where = WhereRecipes()
      where.expression.add(subject)
      where.expression.add(ope)
      where.expression.add(objective)

      recipe.whereTree.add(where)
    }

    syntax.recipe = Optional.of(recipe)
    return Pair(syntax, index)
  }

  private fun parseDelete(tokens: List<String>, startIndex: Int): Pair<QueryRecipe, Int> {
    val recipe = DeleteQueryRecipe()
    var index = startIndex
    val syntax = QueryRecipe(DELETE_QUERY)
    if (tokens[index++].lowercase(Locale.getDefault()) != "from") {
      throw DBRuntimeException("error => " + (tokens[index - 1]))
    }
    recipe.targetTable = tokens[index++]

    if (index >= tokens.size) {
      return Pair(syntax, index)
    }
    if (tokens[index++].lowercase(Locale.getDefault()) != "where") {
      throw DBRuntimeException("error => " + (tokens[index - 1]))
    }
    while (index < tokens.size) {
      val subject = tokens[index++]
      val ope = tokens[index++]
      val objective = tokens[index++]

      val where = WhereRecipes()
      where.expression.add(subject)
      where.expression.add(ope)
      where.expression.add(objective)

      recipe.whereTree.add(where)
    }
    syntax.recipe = Optional.of(recipe)
    return Pair(syntax, index)
  }
}