package com.github.horitaku1124.kotlin.sql_minus.dialect_o.repositories

import com.github.horitaku1124.kotlin.sql_minus.ColumnType
import com.github.horitaku1124.kotlin.sql_minus.DBRuntimeException
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.Column
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.Record
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.RecordCell
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.journals.TableJournal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path


class TableFileMapperTests {
  @Nested
  inner class IntInsert {
    private lateinit var tableMapper: TableFileMapper
    private lateinit var createTempFile: Path
    val cols = listOf("id")

    @BeforeEach
    fun before() {
      val tableJournal = TableJournal("tb1")
      tableJournal.columns = arrayListOf(
        Column().also {
          it.name = "id"
          it.type = ColumnType.INT
        }
      )

      createTempFile = Files.createTempFile("test_", ".table")
      tableMapper = TableFileMapper(tableJournal, createTempFile.toString())
    }

    @Test
    fun oneRecord() {
      tableMapper.insert(cols, Record().also {
        it.cells = arrayListOf(
          RecordCell(ColumnType.INT, "100")
        )
      })

      val allByte = Files.readAllBytes(createTempFile)
      // Header:2 + NullFlags:1 + Int:4 = 7Byte
      assertEquals(7, allByte.size)
      assertEquals(0, allByte[2])
      assertEquals(100, allByte[6])
    }
    @Test
    fun threeRecord() {
      tableMapper.insert(cols, Record().also {
        it.cells = arrayListOf(
          RecordCell(ColumnType.INT, "3")
        )
      })
      tableMapper.insert(cols, Record().also {
        it.cells = arrayListOf(
          RecordCell(ColumnType.INT, "255")
        )
      })
      tableMapper.insert(cols, Record().also {
        it.cells = arrayListOf(
          RecordCell(ColumnType.INT, "256")
        )
      })

      val allByte = Files.readAllBytes(createTempFile)
      // (Header:2 + NullFlags:1 + Int:4) * 3 = 21Byte
      assertEquals(21, allByte.size)
      assertEquals(0, allByte[2])
      assertEquals(3, allByte[6])

      assertEquals(0, allByte[9])
      assertEquals(255, allByte[13].toInt() and 255)

      assertEquals(0, allByte[16])
      assertEquals(1, allByte[19])
      assertEquals(0, allByte[20])
    }
    @Test
    fun nullRecord() {
      tableMapper.insert(cols, Record().also {
        it.cells = arrayListOf(
          RecordCell(ColumnType.INT, "1")
        )
      })
      tableMapper.insert(cols, Record().also {
        it.cells = arrayListOf(
          RecordCell(ColumnType.NULL, "")
        )
      })

      val allByte = Files.readAllBytes(createTempFile)
      // (Header:2 + NullFlags:1 + Int:4) * 2 = 14Byte
      assertEquals(14, allByte.size)
      assertEquals(0, allByte[2])
      assertEquals(1, allByte[6])

      assertEquals(0b1000_0000, allByte[9].toInt().and(255))

    }
  }

  @Nested
  inner class VaryInsert {
    private lateinit var tableMapper: TableFileMapper
    private lateinit var createTempFile: Path
    val cols = listOf("id", "name", "status")
    @BeforeEach
    fun before() {
      val tableJournal = TableJournal("tb1")
      tableJournal.columns = arrayListOf(
        Column().also {
          it.name = "id"
          it.type = ColumnType.INT
        },
        Column().also {
          it.name = "name"
          it.type = ColumnType.VARCHAR
          it.length = 10
        },
        Column().also {
          it.name = "status"
          it.type = ColumnType.SMALLINT
        }
      )

      createTempFile = Files.createTempFile("test_", ".table")
      tableMapper = TableFileMapper(tableJournal, createTempFile.toString())
    }


    @Test
    fun oneRecord() {
      tableMapper.insert(cols, Record().also {
        it.cells = arrayListOf(
          RecordCell(ColumnType.INT, "123"),
          RecordCell(ColumnType.VARCHAR, "abc"),
          RecordCell(ColumnType.SMALLINT, "1")
        )
      })

      val allByte = Files.readAllBytes(createTempFile)
      // Header:2 + NullFlags:1 + Int:4 + Var:10 + SInt:2 = 19Byte
      assertEquals(19, allByte.size)
      assertEquals(0, allByte[2])
      assertEquals(123, allByte[6])
      assertEquals("abc", String(allByte, 7, 3))
      assertEquals(1, allByte[18])
    }
    @Test
    fun oneRecordFull() {
      tableMapper.insert(cols, Record().also {
        it.cells = arrayListOf(
          RecordCell(ColumnType.INT, 0x12345678.toString()),
          RecordCell(ColumnType.VARCHAR, "1234567890"),
          RecordCell(ColumnType.SMALLINT, 0x7890.toString())
        )
      })

      val allByte = Files.readAllBytes(createTempFile)
      // Header:2 + NullFlags:1 + Int:4 + Var:10 + SInt:2 = 19Byte
      assertEquals(19, allByte.size)
      assertEquals(0, allByte[2])
      assertEquals(0x12, allByte[3].toInt() and 0xff)
      assertEquals(0x34, allByte[4].toInt() and 0xff)
      assertEquals(0x56, allByte[5].toInt() and 0xff)
      assertEquals(0x78, allByte[6].toInt() and 0xff)
      assertEquals("1234567890", String(allByte, 7, 10))
      assertEquals(0x78, allByte[17].toInt() and 0xff)
      assertEquals(0x90, allByte[18].toInt() and 0xff)
    }
    @Test
    fun oneRecordOver() {
      assertThrows(DBRuntimeException::class.java) {
        tableMapper.insert(cols, Record().also {
          it.cells = arrayListOf(
            RecordCell(ColumnType.INT, "345"),
            RecordCell(ColumnType.VARCHAR, "12345678901"),
            RecordCell(ColumnType.SMALLINT, "2")
          )
        })

        val allByte = Files.readAllBytes(createTempFile)
      }
    }
  }
}