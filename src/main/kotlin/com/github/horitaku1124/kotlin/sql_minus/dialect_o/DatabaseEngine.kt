package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.*
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.QueryType.*
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.io_mapper.FileMapper
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.io_mapper.YamlFileMapper
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.journals.TableJournal
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.*
import com.github.horitaku1124.kotlin.sql_minus.utils.StringUtil
import java.io.*
import java.nio.file.Files
import java.util.function.Predicate

class DatabaseEngine(var tableMapper: SystemTableFileMapperBuilder) {
  private val DB_PATH = "./db_files"
//  private var fileMapper: FileMapper<DatabaseInformation> = JavaObjectMapper()
  private var fileMapper: FileMapper<DatabaseInformation> = YamlFileMapper()

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
      val dbInfo = chooseDatabase(session)
      val sb = StringBuffer()
      if (dbInfo.tables.isEmpty()) {
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
      return createTable(session, recipe)
    }
    if (syntax.type == DROP_TABLE) {
      return dropTable(session, syntax.subject)
    }
    if (syntax.type == SELECT_QUERY) {
      val recipe = syntax.recipe.get() as SelectQueryRecipe
      return selectQuery(session, recipe)
    }
    if (syntax.type == UPDATE_QUERY) {
      val recipe = syntax.recipe.get() as UpdateQueryRecipe
      return updateQuery(session, recipe)
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
    dbInfo.name = databaseName
    val path = dbFile.toPath().resolve("db.info")

    fileMapper.storeObject(path.toFile(), dbInfo)
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
    session.dbInfo = fileMapper.loadObject(path.toFile())

    session.dbPath = dbFile.toPath()
    return "change Database to -> ${databaseName}\n"
  }

  private fun createTable(session: ClientSession, recipe: CreateTableRecipe): String {
    val dbInfo = chooseDatabase(session)
    val tableName = recipe.name
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
    fileMapper.storeObject(path.toFile(), dbInfo)

    Files.createFile(session.dbPath.resolve(table.fileName))

    return "ok\n"
  }

  private fun dropTable(session: ClientSession, tableName: String): String {
    val dbInfo = chooseDatabase(session)
    val tableInfo = dbInfo.tables.find { t -> t.name == tableName}
        ?: throw DBRuntimeException("table doesn't exist")
    dbInfo.tables.remove(tableInfo)

    Files.delete(session.dbPath.resolve(tableInfo.fileName))

    val path = session.dbPath.resolve("db.info")
    fileMapper.storeObject(path.toFile(), dbInfo)

    return "ok\n"
  }

  private fun insertInto(session: ClientSession, recipe: InsertIntoRecipe): String {
    val dbInfo = chooseDatabase(session)
    val columns = recipe.columns
    val records = recipe.records

    val result = dbInfo.tables.filter { tb ->
      tb.name == recipe.name
    }
    if (result.isEmpty()) {
      throw DBRuntimeException("table doesn't exist -> ${recipe.name}")
    }
    val table = result[0]
    val tableFile = session.dbPath.resolve(table.fileName).toFile()
    if (!tableFile.exists()) {
      throw DBRuntimeException("table journal is gone")
    }
    println(tableFile.absolutePath)

    tableMapper.build(table, tableFile.absolutePath).use { tableMapper ->
      records.forEach { record ->
        tableMapper.insert(columns, record)
      }
    }

    return "OK\n"
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
      val result2 = tableMapper.select(selectParts)
      var result3 = arrayListOf<Record>()
      var compiled = compileWhere(columns, recipe.whereTree)
      for (record in result2) {
        if (compiled.isSatisfied(record)) {
          result3.add(record)
        }
      }
      for(record in result3) {
        for (i in shows) {
          var cell = record.cells[i]
          if (cell.type == ColumnType.VARCHAR) {
            sb.append(cell.textValue)
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

  private fun updateQuery(session: ClientSession, recipe: UpdateQueryRecipe): String {
    val dbInfo = chooseDatabase(session)
    val tableName = recipe.targetTable
    val updates = recipe.updates

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

    val columnToUpdate = hashMapOf<String, UpdatesRecipe>()
    for (upd in updates) {
      columnToUpdate[upd.expression[0]] = upd
    }

    // TODO make it loose couple
    tableMapper.build(table, tableFile.absolutePath).use { tableMapper ->
      val columns = tableMapper.columns()

      val result2 = tableMapper.select(listOf())
      var result3 = arrayListOf<Record>()
      var compiled = compileWhere(columns, recipe.whereTree[0])
      for (record in result2) {
        if (compiled.isSatisfied(record)) {
          result3.add(record)
        }
      }

      var updateCount = 0
      for (record in result3) {
        var updated = false
        for (i in columns.indices) {
          val col = columns[i]
          if (columnToUpdate.containsKey(col.name)) {
            val updateRecipe = columnToUpdate[col.name]!!
            val expression = updateRecipe.expression
            if (expression[1] == "=") {
              val value = expression[2]
              val cell = record.cells[i]
              if (cell.type == ColumnType.INT || cell.type == ColumnType.SMALLINT) {
                cell.intValue = value.toInt()
              } else {
                cell.textValue = value
              }
            }
            updated = true
          }
        }
        tableMapper.update(record)
        if (updated) {
          updateCount++
        }
      }
      return "${updateCount} records updated\n"
    }
  }

  private fun chooseDatabase(session: ClientSession): DatabaseInformation {
    if (session.dbInfo == null) {
      throw DBRuntimeException("DB is not selected")
    }
    return session.dbInfo!!
  }

  private fun compileWhere(columns: List<Column>, recipe: WhereRecipes): WhereVerifyGate {
    if (recipe.expression.size == 3) {
      val colName = recipe.expression[0]
      var colIndex = -1
      for (i in 0 until columns.size) {
        if (columns[i].name == colName) {
          colIndex = i
          break
        }
      }
      return WhereVerifyGate.andRule(colIndex, recipe.expression[1], recipe.expression[2])
    } else {
      return WhereVerifyGate(Predicate<Record> {
        true
      })
    }
  }
}