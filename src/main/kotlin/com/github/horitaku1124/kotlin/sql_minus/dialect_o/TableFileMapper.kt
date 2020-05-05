package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.ColumnType
import com.github.horitaku1124.kotlin.sql_minus.TableMapper
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.journals.TableJournal
import com.github.horitaku1124.kotlin.sql_minus.utils.BinaryBuffer
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer

open class TableFileMapper(private var tableJournal: TableJournal,
                      private var filePath: String): TableMapper {
  private val ReservedLength = 2
  private val RecordLength: Int
  private val NullFlagsLength: Int
  init {
    var recordLength = ReservedLength
    NullFlagsLength = BinaryBuffer.octetOf(tableJournal.columns.size)
    recordLength += NullFlagsLength
    tableJournal.columns.forEach { col ->
      if (col.type == ColumnType.INT) {
        recordLength += 4
      } else if (col.type == ColumnType.VARCHAR) {
        recordLength += col.length!!
      } else if (col.type == ColumnType.SMALLINT) {
        recordLength += 2
      }
    }
    RecordLength = recordLength
  }
  override fun close() {
  }

  private fun recordToBinary(map: HashMap<String, RecordCell>): ByteBuffer {
    val recordBuffer = ByteBuffer.allocate(RecordLength)
    var nullColumns = Array(tableJournal.columns.size) { true } // `true` means NULL

    for (i in tableJournal.columns.indices) {
      val col = tableJournal.columns[i]
      if (map.containsKey(col.name)) {
        nullColumns[i] = false
      }
    }
    var bb = BinaryBuffer(nullColumns)
    var nullFlags = bb.toBytes()

    recordBuffer.put(0x7F).put(0x0F)
    recordBuffer.put(nullFlags)
    tableJournal.columns.forEach { col ->
      var cell: RecordCell? = null
      if (map.containsKey(col.name)) {
        cell = map[col.name]
      }
      if (col.type == ColumnType.INT) {
        if (cell == null) {
          recordBuffer.putInt(0)
        } else {
          recordBuffer.putInt(cell.intValue!!)
        }
      } else if (col.type == ColumnType.VARCHAR) {
        if (cell == null) {
          recordBuffer.position(recordBuffer.position() + col.length!!)
        } else {
          val bytes = cell.textValue!!.toByteArray()
          recordBuffer.put(bytes)
          recordBuffer.position(recordBuffer.position() + col.length!! - bytes.size)
        }
      } else if (col.type == ColumnType.SMALLINT) {
        if (cell == null) {
          recordBuffer.putShort(0)
        } else {
          recordBuffer.putShort(cell.intValue!!.toShort())
        }
      }
    }
    return recordBuffer
  }

  override fun insert(columns: List<String>, record: Record) {
    val map = HashMap<String, RecordCell>()

    for (i in columns.indices) {
      val col = columns[i]
      val cell = record.cells[i]
      map[col] = cell
    }

    val position = File(filePath).length()
    println("position=" + position)

    val recordBuffer = recordToBinary(map)
    val array = recordBuffer.array()
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
      var filePosition = 0L

      while(true) {
        val len = ro.read(buf)
        if (len < 0) break

        var deleted = false
        val recordBuff = ByteBuffer.wrap(buf).also {
          var b0 = it.get().toInt()
          var b1 = it.get().toInt()
          if (b1 == 0b01) {
            deleted = true
          }
        }
        if (deleted) {
          continue
        }

        val record = Record()
        record.position = filePosition

        var nullFlagsByte = ByteArray(NullFlagsLength)
        recordBuff.get(nullFlagsByte)
        var nullFlags = BinaryBuffer.loadFrom(nullFlagsByte, tableJournal.columns.size)

        for (colNum in tableJournal.columns.indices) {
          var col = tableJournal.columns[colNum]

          val cell: RecordCell

          if (col.type == ColumnType.INT) {
            cell = RecordCell(ColumnType.INT, recordBuff.int.toString())
          } else if (col.type == ColumnType.VARCHAR) {
            val buf2 = ByteArray(col.length!!)
            recordBuff.get(buf2)
            var strLen = 0
            for (i in buf2.indices) {
              strLen = i
              if (buf2[i] == 0.toByte()) {
                break
              }
            }

            cell = RecordCell(ColumnType.VARCHAR, String(buf2, 0, strLen))
          } else if (col.type == ColumnType.SMALLINT) {
            cell = RecordCell(ColumnType.SMALLINT, recordBuff.short.toString())
          } else {
            cell = RecordCell(ColumnType.NULL, "")
          }
          if (nullFlags[colNum]) {
            cell.isNull = true
          }
          record.cells.add(cell)
        }
        list.add(record)

        filePosition += len
      }
    }
    return list
  }

  override fun update(record: Record) {
    val map = HashMap<String, RecordCell>()

    for (i in tableJournal.columns.indices) {
      map[tableJournal.columns[i].name] = record.cells[i]
    }
    val recordBuffer = recordToBinary(map)
    println("position=" + record.position)
    RandomAccessFile(File(filePath), "rw").use { ro ->
      ro.skipBytes(record.position!!.toInt())
      ro.write(recordBuffer.array())
    }
  }

  override fun delete(record: Record) {
    println("position=" + record.position)
    RandomAccessFile(File(filePath), "rw").use { ro ->
      ro.skipBytes(record.position!!.toInt())
      ro.writeByte(0x7F)
      ro.writeByte(0b01)
    }
  }
}