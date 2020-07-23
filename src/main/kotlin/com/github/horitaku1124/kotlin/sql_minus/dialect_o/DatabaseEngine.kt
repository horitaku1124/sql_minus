package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.ClientSession
import com.github.horitaku1124.kotlin.sql_minus.ColumnType
import com.github.horitaku1124.kotlin.sql_minus.DBRuntimeException
import com.github.horitaku1124.kotlin.sql_minus.SyntaxTree
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.QueryType.*
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.SelectQueryRecipe
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.repositories.SingleFileRepository
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.repositories.YamlFileMapper

class DatabaseEngine(var tableMapper: SystemTableFileMapperBuilder) {
  private val DB_PATH = "./db_files"
//  private var fileMapper: FileMapper<DatabaseInformation> = JavaObjectMapper()
  private var fileMapper: SingleFileRepository<DatabaseInformation> = YamlFileMapper()
  private var queryCompiler = QueryCompiler()

  private var dbCode = DatabaseEngineCore(tableMapper)

  fun execute(
    syntax: SyntaxTree,
    session: ClientSession
  ): String {
    if (syntax.type == SELECT_QUERY) {
      val recipe = syntax.recipe.get() as SelectQueryRecipe
      return selectQuery(session, recipe)
    } else {
      return dbCode.execute(syntax, session).message
    }
  }

  private fun selectQuery(session: ClientSession, recipe: SelectQueryRecipe): String {
    val dbInfo = chooseDatabase(session)

    val selectParts = recipe.selectParts
    val fromParts = recipe.fromParts
    val tableName = fromParts[0]

    val result = dbInfo.tables.filter { tb ->
      tb.name == tableName
    }
    if (result.isEmpty()) {
      throw DBRuntimeException("table doesn't exist -> ${tableName}")
    }
    val table = result[0]

    val tableFile = session.dbPath.resolve(table.fileName).toFile()
    if (!tableFile.exists()) {
      throw DBRuntimeException("table journal is gone")
    }
    println(tableFile.absolutePath)

    val sb = StringBuffer()
    // TODO make it loose couple
    tableMapper.build(table, tableFile.absolutePath).use { tableMapper ->
      val columns = tableMapper.columns()
      val shows = arrayListOf<Int>()
      if (selectParts.size == 1 && selectParts[0] == "*") {
        for (i in columns.indices) {
          shows.add(i)
        }
      } else {
        for (i in 0 until columns.size) {
          val name = columns[i].name
          if (selectParts.contains(name)) {
            val index = selectParts.indexOf(name)
            shows.add(index)
          }
        }
      }
      for (i in shows) {
        sb.append(columns[i].name)
        sb.append('\t')
      }
      sb.append('\n')
      sb.append("-".repeat(20))
      sb.append('\n')
      val fullScannedRecords = tableMapper.select(selectParts)
      var satisfiedRecords = arrayListOf<Record>()
      var compiled = queryCompiler.compileWhere(columns, recipe.whereTree)
      for (record in fullScannedRecords) {
        if (compiled.isSatisfied(record)) {
          satisfiedRecords.add(record)
        }
      }
      for(record in satisfiedRecords) {
        for (i in shows) {
          var cell = record.cells[i]
          if (cell.isNull) {
            sb.append("NULL")
          } else if (cell.type == ColumnType.VARCHAR) {
            sb.append(cell.textValue)
          } else if (cell.type == ColumnType.NUMBER) {
            sb.append(cell.numberValue)
          } else {
            sb.append(cell.intValue!!.toInt())
          }
          sb.append('\t')
        }
        sb.append('\n')
      }
    }
    return sb.toString()
  }

  private fun chooseDatabase(session: ClientSession): DatabaseInformation {
    if (session.dbInfo == null) {
      throw DBRuntimeException("DB is not selected")
    }
    return session.dbInfo!!
  }
}