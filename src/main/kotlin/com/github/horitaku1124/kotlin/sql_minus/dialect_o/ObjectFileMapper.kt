package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.ColumnType
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.journals.TableJournal
import java.io.*
import java.nio.file.Files

class ObjectFileMapper {
  fun readData(file: File):DatabaseInformation {
    // TODO It is not supported by GraalVM

    return FileInputStream(file).use { fi ->
      ObjectInputStream(fi).use { oi ->
        oi.readObject() as DatabaseInformation
      }
    }
  }

  fun writeData(file: File, dbInfo: DatabaseInformation) {
    // TODO It is not supported by GraalVM

    FileOutputStream(file).use { fo ->
      ObjectOutputStream(fo).use { ow ->
        ow.writeObject(dbInfo)
      }
    }
  }


  fun writeDataYaml(file: File, dbInfo: DatabaseInformation) {
    FileOutputStream(file).use { os ->
      val sb = StringBuffer()
      sb.append("name: " + dbInfo.name).append("\n")
      sb.append("\n")

      dbInfo.tables.forEach {tb ->
        sb.append("- table: " + tb.name).append("\n")
        sb.append("  fileName: " + tb.fileName).append("\n")
        tb.columns.forEach {col ->
          sb.append("  - column: " + col.name).append("\n")
          sb.append("    type: " + col.type).append("\n")
          if (col.type == ColumnType.VARCHAR) {
            sb.append("    length: " + col.length).append("\n")
          } else {
            sb.append("    length:").append("\n")
          }
        }
        sb.append("\n")
      }
      println (file.name + " -> " + sb.length + " bytes")

      os.write(sb.toString().toByteArray())
    }
  }

  fun readDataYaml(file: File):DatabaseInformation {
    val dbInfo = DatabaseInformation()
    val lines = Files.readAllLines(file.toPath())
    var index = 0
    while (index < lines.size) {
      var line = lines[index++]
      if (line.trim() == "") {
        continue
      }
      if (line.startsWith("name:")) {
        dbInfo.name = line.replace("name:", "").trim()
        continue
      }
      if (line.startsWith("- table:")) {
        val tableName = line.replace("- table:", "").trim()
        val table = TableJournal(tableName)
        line = lines[index++].trim()
        if (line.startsWith("fileName:")) {
          table.fileName = line.replace("fileName:", "").trim()
        }
        while (index < lines.size) {
          line = lines[index++].trim()
          if (line.startsWith("- column:")) {
            val col = Column()
            col.name = line.replace("- column: ", "")
            val type = lines[index++].trim().replace("type: ", "")
            val length = lines[index++].trim().replace("length: ", "")

            col.type = ColumnType.valueOf(type)
            if (col.type == ColumnType.VARCHAR) {
              col.length = length.toInt()
            }
            table.columns.add(col)
          } else {
            break
          }
        }
        dbInfo.tables.add(table)
      }
    }
    return dbInfo
  }
}