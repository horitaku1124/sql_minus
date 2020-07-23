package com.github.horitaku1124.kotlin.sql_minus.dialect_o.repositories

import com.github.horitaku1124.kotlin.sql_minus.ColumnType
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.Column
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.Record
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.RecordCell
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.journals.TableJournal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
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
}