package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.horitaku1124.kotlin.sql_minus.ColumnType
import com.github.horitaku1124.kotlin.sql_minus.DBRuntimeException
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.ExecuteResult.ExecuteResultBuilder
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.QueryType.*
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.repositories.SingleFileRepository
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.repositories.YamlFileMapper
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.journals.TableJournal
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.*
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.SelectInvocationRecipe.*
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.SelectInvocationRecipe.InvocationTask.TaskType.*
import com.github.horitaku1124.kotlin.sql_minus.utils.StringUtil
import java.io.File
import java.nio.file.Files
import java.util.Stack

/**
 * how to retrieve and update data into file
 * This class must know real file path(?)
 * This class must not manipulate file data
 */
class DatabaseEngineCore(var tableMapper: SystemTableFileMapperBuilder) {
  private val DB_PATH = "./db_files" // TODO Should be inserted from outside
//  private var fileMapper: FileMapper<DatabaseInformation> = JavaObjectMapper()
  private var fileMapper: SingleFileRepository<DatabaseInformation> = YamlFileMapper()
  private var queryCompiler = QueryCompiler()

  fun execute(
    syntax: QueryRecipe,
    session: ClientSession
  ): ExecuteResult {
    val resultBuilder = ExecuteResultBuilder.builder()
      .setStatus(ExecuteResult.ResultStatus.OK)
      .setQueryType(syntax.type)

    if (syntax.type == CREATE_DATABASE) {
      val message = createDatabase(session, syntax.subject)
      return resultBuilder.setMessage(message).build()
    }
    if (syntax.type == CHANGE_DATABASE) {
      val message = changeDatabase(session, syntax.subject)
      return resultBuilder.setMessage(message).build()
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
      return resultBuilder.setMessage(sb.toString()).build()
    }
    if (syntax.type == INSERT_QUERY) {
      val message = insertInto(session, syntax.recipe.get() as InsertIntoRecipe)
      return resultBuilder.setMessage(message).build()
    }
    if (syntax.type == CREATE_TABLE) {
      val recipe = syntax.recipe.get() as CreateTableRecipe
      val message = createTable(session, recipe)
      return resultBuilder.setMessage(message).build()
    }
    if (syntax.type == DROP_TABLE) {
      val message = dropTable(session, syntax.subject)
      return resultBuilder.setMessage(message).build()
    }
    if (syntax.type == UPDATE_QUERY) {
      val recipe = syntax.recipe.get() as UpdateQueryRecipe
      val message = updateQuery(session, recipe)
      return resultBuilder.setMessage(message).build()
    }
    if (syntax.type == DELETE_QUERY) {
      val recipe = syntax.recipe.get() as DeleteQueryRecipe
      val message = deleteQuery(session, recipe)
      return resultBuilder.setMessage(message).build()
    }
    if (syntax.type == SELECT_QUERY) {
      val recipe = syntax.recipe.get() as SelectQueryRecipe
      val resultRecords = selectQuery(session, recipe)
      val mapper = ObjectMapper()
      // TODO should be Protocol Buffer
      val json = mapper.writeValueAsString(resultRecords)
      return resultBuilder.setMessage(json).build()
    }

    if (syntax.type == SELECT_QUERY2) {
      val resultRecords = selectQuery2(session, syntax.recipe.get() as SelectInvocationRecipe)
      val mapper = ObjectMapper()
      // TODO should be Protocol Buffer
      val json = mapper.writeValueAsString(resultRecords)
      return resultBuilder.setMessage(json).build()
    }

    return ExecuteResultBuilder.errorCase()
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

    tableMapper.build(table, session.dbPath.resolve(table.fileName).toString()).createTable()

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

  private fun selectQuery(session: ClientSession, recipe: SelectQueryRecipe): List<Map<String, String>> {
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
      val fullScannedRecords = tableMapper.select(selectParts)
      val satisfiedRecords = arrayListOf<Record>()
      val compiled = queryCompiler.compileWhere(columns, recipe.whereTree)
      for (record in fullScannedRecords) {
        if (compiled.isSatisfied(record)) {
          satisfiedRecords.add(record)
        }
      }
      val resultMapList = arrayListOf<Map<String, String>>()
      for(record in satisfiedRecords) {
        val recordMap = hashMapOf<String, String>()
        for (i in shows) {
          val col = columns[i]
          val cell = record.cells[i]

          recordMap.put(col.name,
            if (cell.isNull) {
              ""
            } else if (cell.type == ColumnType.VARCHAR) {
              cell.textValue!!
            } else if (cell.type == ColumnType.NUMBER) {
              cell.numberValue.toString()
            } else {
              cell.intValue!!.toString()
            }
          )
        }
        resultMapList.add(recordMap)
      }
      return resultMapList
    }
  }


  private fun selectQuery2(session: ClientSession, recipe: SelectInvocationRecipe): List<Map<String, String>> {
    val dbInfo = chooseDatabase(session)
    val resultMapList = arrayListOf<Map<String, String>>()

    var lastRecords = Stack<HashMap<String, Pair<List<Column>, List<Record>>>>()
    for (task in recipe.tasks) {
      if (task.type == TableRead) {
        val layerRecords = hashMapOf<String, Pair<List<Column>, List<Record>>>()

        val tableRead = task as TableReadTask
        val tableName = tableRead.tableName

        val table = dbInfo.tables.filter { tb ->
          tb.name == tableName
        }.also {
          if (it.isEmpty()) {
            throw DBRuntimeException("table doesn't exist -> $tableName")
          }
        }.first()

        val tableFile = session.dbPath.resolve(table.fileName).toFile()

        if (!tableFile.exists()) {
          throw DBRuntimeException("table journal is gone")
        }

        tableMapper.build(table, tableFile.absolutePath).use { tableMapper ->
          val columns = tableMapper.columns()

          val records = tableMapper.select(listOf())

          layerRecords.put(tableRead.alias, Pair(columns, records))
        }

        lastRecords.push(layerRecords)
      }

      if (task.type == Filtering) {
        val tableFilter = task as FilteringTask
        if (lastRecords.isEmpty()) {
          throw DBRuntimeException("last result was none")
        }
        if (tableFilter.wheres.expression.size > 0) {
          var verifyRecords = lastRecords.pop()
          var key = verifyRecords.keys.stream().findFirst().get()
          var columns = verifyRecords[key]!!.first
          var records = verifyRecords[key]!!.second

          var satisfiedRecords = arrayListOf<Record>()

          val compiled = queryCompiler.compileWhere(columns, tableFilter.wheres)
          for (record in records) {
            if (compiled.isSatisfied(record)) {
              satisfiedRecords.add(record)
            }
          }
          verifyRecords.put(key,Pair(columns, satisfiedRecords))
          lastRecords.push(verifyRecords)
        }
      }

      if (task.type == Selection) {
        val selectTask = task as SelectionTask
        var selectParts = selectTask.columns

        var verifyRecords = lastRecords.pop()
        var key = verifyRecords.keys.stream().findFirst().get()
        var columns = verifyRecords[key]!!.first
        var records = verifyRecords[key]!!.second

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

        for(record in records) {
          val recordMap = hashMapOf<String, String>()
          for (i in shows) {
            val col = columns[i]
            val cell = record.cells[i]

            recordMap.put(col.name,
              if (cell.isNull) {
                ""
              } else if (cell.type == ColumnType.VARCHAR) {
                cell.textValue!!
              } else if (cell.type == ColumnType.NUMBER) {
                cell.numberValue.toString()
              } else {
                cell.intValue!!.toString()
              }
            )
          }
          resultMapList.add(recordMap)
        }
      }
    }

    return resultMapList
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
      val result3 = arrayListOf<Record>()
      val compiled = queryCompiler.compileWhere(columns, recipe.whereTree[0])
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

  private fun deleteQuery(session: ClientSession, recipe: DeleteQueryRecipe): String {
    val dbInfo = chooseDatabase(session)
    val tableName = recipe.targetTable

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

    // TODO make it loose couple
    tableMapper.build(table, tableFile.absolutePath).use { tableMapper ->
      val columns = tableMapper.columns()

      val result2 = tableMapper.select(listOf())
      var result3 = arrayListOf<Record>()
      var compiled = queryCompiler.compileWhere(columns, recipe.whereTree[0])
      for (record in result2) {
        if (compiled.isSatisfied(record)) {
          result3.add(record)
        }
      }

      var updateCount = 0
      for (record in result3) {
        var updated = false
        tableMapper.delete(record)
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
}