package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.ColumnType
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.journals.TableJournal
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer

class TableIOMapper(private var tableJournal: TableJournal,
                    private var filePath: String): AutoCloseable {
  private val ReservedLength = 4
  private val RecordLength: Int
  init {
    var recordLength = ReservedLength
    tableJournal.columns.forEach { col ->
      if (col.type == ColumnType.INT) {
        recordLength += 4
      } else if (col.type == ColumnType.VARCHAR) {
        recordLength += col.length!!
      }
    }
    RecordLength = recordLength
  }
  override fun close() {
  }

  fun insert(columns: List<String>, record: Record) {
    val map = HashMap<String, RecordCell>()

    for (i in columns.indices) {
      val col = columns[i]
      val cell = record.cells[i]
      map[col] = cell
    }

    val buffer = ByteBuffer.allocate(RecordLength)
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
      } else if (col.type == ColumnType.VARCHAR) {
        if (cell == null) {
          buffer.position(buffer.position() + col.length!!)
        } else {
          buffer.put(cell.value.toByteArray())
        }
      }
    }
    val position = File(filePath).length()
    println("position=" + position)

    val array = buffer.array()
    println("buffer=" + array.size)

    RandomAccessFile(File(filePath), "rw").use { ro ->
      ro.skipBytes(position.toInt())
      ro.write(array)
    }
  }

  fun select(columns: List<String>): List<Record> {
    var list = arrayListOf<Record>()
    RandomAccessFile(File(filePath), "rw").use { ro ->
      var buf = ByteArray(RecordLength)

      while(true) {
        var len = ro.read(buf)
        if (len < 0) break

        var bytes = ByteBuffer.wrap(buf)
        bytes.position(ReservedLength)
        println(len)
        var record = Record()

        tableJournal.columns.forEach { col ->
          var cell: RecordCell

          if (col.type == ColumnType.INT) {
            cell = RecordCell(ColumnType.INT, bytes.getInt().toString())
          } else if (col.type == ColumnType.VARCHAR) {
            var buf2 = ByteArray(col.length!!)
            bytes.get(buf2)

            cell = RecordCell(ColumnType.VARCHAR, String(buf2))
          } else {
            cell = RecordCell(ColumnType.NULL, "")
          }
          record.cells.add(cell)
        }
        list.add(record)
      }
    }
    return list
  }
}