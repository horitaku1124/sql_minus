package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.ClientSession
import com.github.horitaku1124.kotlin.sql_minus.DBRuntimeException
import com.github.horitaku1124.kotlin.sql_minus.SyntaxTree
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.QueryType.*
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.journals.TableJournal
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.CreateTableRecipe
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
      val sb = StringBuffer()
      if (session.dbInfo.tables.isEmpty()) {
        sb.append("no tables\n")
      } else {
        session.dbInfo.tables.forEach { tb ->
          sb.append(tb.name).append("\n")
        }
      }
      return sb.toString()
    }
    if (syntax.type == CREATE_TABLE) {
      val recipe = syntax.recipe.get() as CreateTableRecipe
      return createTable(recipe, session)
    }

    return "Error:"
  }

  fun createDatabase(session: ClientSession, databaseName: String): String {
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

  fun changeDatabase(session: ClientSession, databaseName: String): String {
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

  fun createTable(recipe: CreateTableRecipe, session: ClientSession): String {
    val tableName = recipe.name
    val tableNames = session.dbInfo.tables.map { t -> t.name }
    if (tableNames.contains(tableName)) {
      throw DBRuntimeException("table already exists")
    }
    val table = TableJournal(tableName)
    table.columns.addAll(recipe.columns) // TODO to deep copy
    session.dbInfo.tables.add(table)

    val hash = StringUtil.hash(tableName)
    table.fileName = hash + "_" + tableName


    val path = session.dbPath.resolve("db.info")
    fileMapper.writeData(path.toFile(), session.dbInfo)

    Files.createFile(session.dbPath.resolve(table.fileName))

    return "ok\n"
  }
}