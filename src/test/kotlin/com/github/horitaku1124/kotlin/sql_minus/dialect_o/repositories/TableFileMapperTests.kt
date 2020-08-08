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
import java.time.LocalDate
import java.time.LocalDateTime


class TableFileMapperTests {
  @Nested
  inner class IntInsert {
    private lateinit var tableMapper: TableFileMapper
    private lateinit var createTempFile: Path
    private val cols = listOf("id")

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
      assertEquals(0, allByte[2]) // Is Not Null
      assertEquals(100, allByte[6]) // value
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
          RecordCell(ColumnType.NULL)
        )
      })

      val allByte = Files.readAllBytes(createTempFile)
      // (Header:2 + NullFlags:1 + Int:4) * 2 = 14Byte
      assertEquals(14, allByte.size)
      assertEquals(0, allByte[2]) // Is Not Null
      assertEquals(1, allByte[6]) // Value

      assertEquals(0b1000_0000, allByte[9].toInt().and(255)) // Is Null

    }
  }

  @Nested
  inner class DateInsert {
    private lateinit var tableMapper: TableFileMapper
    private lateinit var createTempFile: Path
    private val cols = listOf("id", "created_at", "updated_at")

    @BeforeEach
    fun before() {
      val tableJournal = TableJournal("tb1")
      tableJournal.columns = arrayListOf(
        Column().also {
          it.name = "id"
          it.type = ColumnType.INT
        },
        Column().also {
          it.name = "created_at"
          it.type = ColumnType.TIMESTAMP
        },
        Column().also {
          it.name = "updated_at"
          it.type = ColumnType.DATE
        }
      )

      createTempFile = Files.createTempFile("test_", ".table")
      tableMapper = TableFileMapper(tableJournal, createTempFile.toString())
    }

    @Test
    fun oneRecord() {
      tableMapper.insert(cols, Record().also {
        it.cells = arrayListOf(
          RecordCell(ColumnType.INT, "100"),
          RecordCell(ColumnType.TIMESTAMP, "2020-10-31 12:45:32"),
          RecordCell(ColumnType.DATE, "1951-09-22")
        )
      })

      val allByte = Files.readAllBytes(createTempFile)
      // Header:2 + NullFlags:1 + Int:4 + Timestamp-Int:4 + Date-Int:4 = 15Byte
      assertEquals(15, allByte.size)
      assertEquals(0, allByte[2]) // Is Not Null
      assertEquals(100, allByte[6]) // value
    }

    @Test
    fun selectOneRecord() {
      tableMapper.insert(cols, Record().also {
        it.cells = arrayListOf(
          RecordCell(ColumnType.INT, "100"),
          RecordCell(ColumnType.TIMESTAMP, "2020-10-31 12:45:32"),
          RecordCell(ColumnType.DATE, "1951-09-22")
        )
      })

      val records = tableMapper.select(listOf())
      assertEquals(1, records.size)
      records[0].let { record ->
        assertEquals(100, record.cells[0].intValue)
        assertEquals(LocalDateTime.of(2020, 10, 31, 12, 45, 32),
          record.cells[1].getTimeStampValue())
        assertEquals(LocalDate.of(1951, 9, 22), record.cells[2].getDateValue())
      }
    }
  }

  @Nested
  inner class VaryInsert {
    private lateinit var tableMapper: TableFileMapper
    private lateinit var createTempFile: Path
    private val cols = listOf("id", "name", "status")
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

  @Nested
  inner class SelectRecord {
    private lateinit var tableMapper: TableFileMapper
    private lateinit var createTempFile: Path
    private val cols = listOf("id", "name", "status")
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

      val records = tableMapper.select(listOf())

      assertEquals(1, records.size)
      records[0].let { record ->
        assertEquals(123, record.cells[0].intValue)
        assertEquals("abc", record.cells[1].textValue)
        assertEquals(1, record.cells[2].intValue)
      }
    }
    @Test
    fun threeRecords() {
      tableMapper.insert(cols, Record().also {
        it.cells = arrayListOf(
          RecordCell(ColumnType.INT, "1001"),
          RecordCell(ColumnType.VARCHAR, "abc"),
          RecordCell(ColumnType.SMALLINT, "1")
        )
      })
      tableMapper.insert(cols, Record().also {
        it.cells = arrayListOf(
          RecordCell(ColumnType.INT, "1002"),
          RecordCell(ColumnType.VARCHAR, "def"),
          RecordCell(ColumnType.SMALLINT, "2")
        )
      })
      tableMapper.insert(cols, Record().also {
        it.cells = arrayListOf(
          RecordCell(ColumnType.INT, "1003"),
          RecordCell(ColumnType.VARCHAR, "ghi"),
          RecordCell(ColumnType.SMALLINT, "3")
        )
      })

      val records = tableMapper.select(listOf())

      assertEquals(3, records.size)
      records[0].let { record ->
        assertEquals(1001, record.cells[0].intValue)
        assertEquals("abc", record.cells[1].textValue)
        assertEquals(1, record.cells[2].intValue)
      }
      records[1].let { record ->
        assertEquals(1002, record.cells[0].intValue)
        assertEquals("def", record.cells[1].textValue)
        assertEquals(2, record.cells[2].intValue)
      }
      records[2].let { record ->
        assertEquals(1003, record.cells[0].intValue)
        assertEquals("ghi", record.cells[1].textValue)
        assertEquals(3, record.cells[2].intValue)
      }
    }
  }

  @Nested
  inner class UpdateRecord {
    private lateinit var tableMapper: TableFileMapper
    private lateinit var createTempFile: Path
    private val cols = listOf("id", "name", "status")
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

      val records = tableMapper.select(listOf())

      assertEquals(1, records.size)

      records[0].let { record ->
        record.cells[1].textValue = "ABCDEFG"
        tableMapper.update(record)
      }

      val records1 = tableMapper.select(listOf())

      assertEquals(1, records1.size)
      records1[0].let { record ->
        assertEquals(123, record.cells[0].intValue)
        assertEquals("ABCDEFG", record.cells[1].textValue)
        assertEquals(1, record.cells[2].intValue)
      }
    }
  }

  @Nested
  inner class DeleteRecord {
    private lateinit var tableMapper: TableFileMapper
    private lateinit var createTempFile: Path
    private val cols = listOf("id")
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
          RecordCell(ColumnType.INT, "123")
        )
      })

      val records = tableMapper.select(listOf())

      assertEquals(1, records.size)

      tableMapper.delete(records[0])

      val records1 = tableMapper.select(listOf())

      assertEquals(0, records1.size)
    }
  }
}