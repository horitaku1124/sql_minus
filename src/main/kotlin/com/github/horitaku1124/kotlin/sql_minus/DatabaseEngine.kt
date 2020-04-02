package com.github.horitaku1124.kotlin.sql_minus

import com.github.horitaku1124.kotlin.sql_minus.QueryType.CHANGE_DATABASE
import com.github.horitaku1124.kotlin.sql_minus.QueryType.CREATE_DATABASE
import java.io.File
import java.lang.RuntimeException
import java.util.*

class DatabaseEngine {
  private val DB_PATH = "./db_files"

  fun execute(
    syntax: SyntaxTree,
    session: ClientSession
  ) {
    if (syntax.type == CREATE_DATABASE) {
      createDatabase(syntax.subject[0])
    }
    if (syntax.type == CHANGE_DATABASE) {
      changeDatabase(session, syntax.subject[0])
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