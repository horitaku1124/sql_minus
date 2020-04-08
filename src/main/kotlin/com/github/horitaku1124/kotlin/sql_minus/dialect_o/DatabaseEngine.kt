package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.ClientSession
import com.github.horitaku1124.kotlin.sql_minus.DBRuntimeException
import com.github.horitaku1124.kotlin.sql_minus.SyntaxTree
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.QueryType.*
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.journals.TableJournal
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.CreateTableRecipe
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.InsertIntoRecipe
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.SelectQueryRecipe
import com.github.horitaku1124.kotlin.sql_minus.utils.StringUtil
import java.io.*
import java.nio.file.Files

class DatabaseEngine {
  private val DB_PATH = "./db_files"
  private var fileMapper = ObjectFileMapper()

  fun execute(
    syntax: SyntaxTree,
    session: ClientSession
  ): String {
    if (syntax.type == CREATE_DATABASE) {
      return createDatabase(session, syntax.subject)
    }
    if (syntax.type == CHANGE_DATABASE) {
      return changeDatabase(session, syntax.subject)
    }
    if (syntax.type == SHOW_TABLES) {
      if (session.dbInfo == null) {
        throw DBRuntimeException("DB is not selected")
      }
      val sb = StringBuffer()
      if (session.dbInfo!!.tables.isEmpty()) {
        sb.append("no tables\n")
      } else {
        session.dbInfo!!.tables.forEach { tb ->
          sb.append(tb.name).append("\n")
        }
      }
      return sb.toString()
    }
    if (syntax.type == INSERT_QUERY) {
      return insertInto(session, syntax.recipe.get() as InsertIntoRecipe)
    }
    if (syntax.type == CREATE_TABLE) {
      val recipe = syntax.recipe.get() as CreateTableRecipe
      return createTable(recipe, session)
    }
    if (syntax.type == SELECT_QUERY) {
      var recipe = syntax.recipe.get() as SelectQueryRecipe
      return selectQuery(session, recipe)
    }

    return "Error:"
  }

  private fun createDatabase(session: ClientSession, databaseName: String): String {
    val dbFile = File(DB_PATH + "/" + databaseName)
    if (dbFile.exists()) {
      throw DBRuntimeException("db already exists")
    }
    println("createDatabase -> " + databaseName)
    dbFile.mkdir()

    val dbInfo = DatabaseInformation()
    val path = dbFile.toPath().resolve("db.info")

    fileMapper.writeData(path.toFile(), dbInfo)
    session.dbPath = dbFile.toPath()
    return "created database -> ${databaseName}\n"
  }

  private fun changeDatabase(session: ClientSession, databaseName: String): String {
    val dbFile = File(DB_PATH + "/" + databaseName)
    if (!dbFile.exists()) {
      throw DBRuntimeException("db doesn't exist")
    }
    println("change Database to -> $databaseName")
    session.setCurrentDatabase(databaseName)

    val path = dbFile.toPath().resolve("db.info")
    session.dbInfo = fileMapper.readData(path.toFile())

    session.dbPath = dbFile.toPath()
    return "change Database to -> ${databaseName}\n"
  }

  private fun createTable(recipe: CreateTableRecipe, session: ClientSession): String {
    val tableName = recipe.name
    if (session.dbInfo == null) {
      throw DBRuntimeException("DB is not selected")
    }
    val dbInfo = session.dbInfo!!
    val tableNames = dbInfo.tables.map { t -> t.name }
    if (tableNames.contains(tableName)) {
      throw DBRuntimeException("table already exists")
    }
    val table = TableJournal(tableName)
    table.columns.addAll(recipe.columns) // TODO to deep copy
    dbInfo.tables.add(table)

    val hash = StringUtil.hash(tableName)
    table.fileName = hash + "_" + tableName


    val path = session.dbPath.resolve("db.info")
    fileMapper.writeData(path.toFile(), dbInfo)

    Files.createFile(session.dbPath.resolve(table.fileName))

    return "ok\n"
  }

  private fun insertInto(session: ClientSession, recipe: InsertIntoRecipe): String {
    val columns = recipe.columns
    val records = recipe.records
    if (session.dbInfo == null) {
      throw DBRuntimeException("DB is not selected")
    }
    val dbInfo = session.dbInfo!!

    var result = dbInfo.tables.filter { tb ->
      tb.name == recipe.name
    }
    if (result.isEmpty()) {
      throw DBRuntimeException("table doesn't exist -> ${recipe.name}")
    }
    var table = result[0]
    val tableFile = session.dbPath.resolve(table.fileName).toFile()
    if (!tableFile.exists()) {
      throw DBRuntimeException("table journal is gone")
    }
    println(tableFile.absolutePath)

    TableIOMapper(table, tableFile.absolutePath).use { tableMapper ->
      records.forEach { record ->
        tableMapper.insert(columns, record)
      }
    }

    return "OK\n"
  }

  private fun selectQuery(session: ClientSession, recipe: SelectQueryRecipe): String {
    if (session.dbInfo == null) {
      throw DBRuntimeException("DB is not selected")
    }
    val dbInfo = session.dbInfo!!

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
    TableIOMapper(table, tableFile.absolutePath).use { tableMapper ->
      val columns = tableMapper.columns()
      val shows = arrayListOf<Int>()
      if (selectParts.size == 1 && selectParts[0] == "*") {
        for (i in columns.indices) {
          shows.add(i)
        }
      } else {
        for (i in 0 until columns.size) {
          var name = columns[i].name
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
      val result2 = tableMapper.select(selectParts)
      for(record in result2) {
        for (i in shows) {
          sb.append(record.cells[i].value)
          sb.append('\t')
        }
        sb.append('\n')
      }
    }
    return sb.toString()
  }
}