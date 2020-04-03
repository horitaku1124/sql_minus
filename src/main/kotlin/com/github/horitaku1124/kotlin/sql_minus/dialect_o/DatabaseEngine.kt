package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.ClientSession
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.QueryType.CHANGE_DATABASE
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.QueryType.CREATE_DATABASE
import com.github.horitaku1124.kotlin.sql_minus.SyntaxTree
import java.io.File
import java.lang.RuntimeException

class DatabaseEngine {
  private val DB_PATH = "./db_files"

  fun execute(
    syntax: SyntaxTree,
    session: ClientSession
  ) {
    if (syntax.type == CREATE_DATABASE) {
      createDatabase(syntax.subject)
    }
    if (syntax.type == CHANGE_DATABASE) {
      changeDatabase(session, syntax.subject)
    }
  }

  fun createDatabase(databaseName: String) {
    val dbFile = File(DB_PATH + "/" + databaseName)
    if (dbFile.exists()) {
      throw RuntimeException("db already exists")
    }
    println("createDatabase -> " + databaseName)
    dbFile.mkdir()
  }

  fun changeDatabase(session: ClientSession, databaseName: String) {
    val dbFile = File(DB_PATH + "/" + databaseName)
    if (!dbFile.exists()) {
      throw RuntimeException("db doesn't exist")
    }
    println("change Database to -> " + databaseName)
    session.setCurrentDatabase(databaseName)
  }
}