package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.ColumnType
import com.github.horitaku1124.kotlin.sql_minus.TableMapper
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.journals.TableJournal
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer

class TableFileMapper(private var tableJournal: TableJournal,
                      private var filePath: String): TableMapper {
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

  override fun insert(columns: List<String>, record: Record) {
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

  override fun columns(): List<Column> {
    val list = arrayListOf<Column>()
    list.addAll(tableJournal.columns)
    return list
  }

  override fun select(columns: List<String>): List<Record> {
    val list = arrayListOf<Record>()
    RandomAccessFile(File(filePath), "rw").use { ro ->
      val buf = ByteArray(RecordLength)

      while(true) {
        val len = ro.read(buf)
        if (len < 0) break

        val bytes = ByteBuffer.wrap(buf)
        bytes.position(ReservedLength)
        val record = Record()

        tableJournal.columns.forEach { col ->
          val cell: RecordCell

          if (col.type == ColumnType.INT) {
            cell = RecordCell(ColumnType.INT, bytes.int.toString())
          } else if (col.type == ColumnType.VARCHAR) {
            val buf2 = ByteArray(col.length!!)
            bytes.get(buf2)
            var strLen = 0
            for (i in buf2.indices) {
              strLen = i
              if (buf2[i] == 0.toByte()) {
                break
              }
            }

            cell = RecordCell(ColumnType.VARCHAR, String(buf2, 0, strLen))
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