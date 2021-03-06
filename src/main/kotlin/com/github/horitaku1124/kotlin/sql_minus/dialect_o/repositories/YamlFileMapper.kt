package com.github.horitaku1124.kotlin.sql_minus.dialect_o.repositories

import com.github.horitaku1124.kotlin.sql_minus.ColumnType
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.Column
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.DatabaseInformation
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.journals.TableJournal
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files

class YamlFileMapper: SingleFileRepository<DatabaseInformation> {
  override fun loadObject(file: File): DatabaseInformation {
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
            if (col.type == ColumnType.NUMBER) {
              val numFormat = lines[index++].trim().replace("numberFormat: ", "")
              numFormat.split(",").let {
                col.numberFormat = Pair(it[0].toInt(), it[1].toInt())
              }
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

  override fun storeObject(file: File, obj: DatabaseInformation) {
    FileOutputStream(file).use { os ->
      val sb = StringBuffer()
      sb.append("name: " + obj.name).append("\n")
      sb.append("\n")

      obj.tables.forEach {tb ->
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
          if (col.type == ColumnType.NUMBER) {
            sb
              .append("    numberFormat: ")
              .append(col.numberFormat!!.first)
              .append(",")
              .append(col.numberFormat!!.second)
              .append("\n")
          }
        }
        sb.append("\n")
      }
      println (file.name + " -> " + sb.length + " bytes")

      os.write(sb.toString().toByteArray())
    }
  }
}