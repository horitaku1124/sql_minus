package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.ColumnType
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.journals.TableJournal
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer

class TableIOMapper(var tableJournal: TableJournal, var filePath: String): AutoCloseable {
  private val ReservedLength = 4
  private val RecordLength: Int
  init {
    var recordLength = ReservedLength
    tableJournal.columns.forEach { col ->
      if (col.type == ColumnType.INT) {
        recordLength += 4
      }
    }
    RecordLength = recordLength
  }
  override fun close() {
  }

  fun insert(columns: List<String>, record: Record) {
    var map = HashMap<String, RecordCell>()

    for (i in columns.indices) {
      var col = columns[i]
      var cell = record.cells[i]
      map.put(col, cell)
    }

    var buffer = ByteBuffer.allocate(RecordLength)
    buffer.putInt(12345)
    tableJournal.columns.forEach { col ->
      var cell: RecordCell? = null
      if (map.containsKey(col.name)) {
        cell = map[col.name]
      }
      if (col.type == ColumnType.INT) {
        if (cell == null) {
          buffer.putInt(0)
        } else {
          buffer.putInt(cell.value.toInt())
        }
      }
    }
    val position = File(filePath).length()
    println("position=" + position)

    var array = buffer.array()
    println("buffer=" + array.size)

    RandomAccessFile(File(filePath), "rw").use { ro ->
      ro.skipBytes(position.toInt())
      ro.write(array)
    }
  }
}