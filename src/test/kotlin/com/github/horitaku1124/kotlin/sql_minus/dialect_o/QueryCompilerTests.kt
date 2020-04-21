package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.ColumnType.*
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.WhereRecipes
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class QueryCompilerTests {
  lateinit var queryCompiler: QueryCompiler
  lateinit var columns: List<Column>

  @BeforeEach
  fun before() {
    queryCompiler = QueryCompiler()

    columns = arrayListOf(Column("id", INT))
  }
  @Test
  fun compileWhereTest1() {
    val recipe = WhereRecipes()
    val compiled = queryCompiler.compileWhere(columns, recipe)

    val record = Record().also {
      it.cells.add(RecordCell(INT, "1"))
    }

    assertEquals(true, compiled.isSatisfied(record))
  }
  @Test
  fun compileWhereTest2() {
    val recipe = WhereRecipes()
    recipe.expression.addAll(listOf("id", "=", "1"))
    val compiled = queryCompiler.compileWhere(columns, recipe)

    val record1 = Record().also {
      it.cells.add(RecordCell(INT, "1"))
    }
    val record2 = Record().also {
      it.cells.add(RecordCell(INT, "2"))
    }

    assertEquals(true, compiled.isSatisfied(record1))
    assertEquals(false, compiled.isSatisfied(record2))
  }
  @Test
  fun compileWhereTest3() {
    val recipe = WhereRecipes()
    recipe.expression.addAll(listOf("id", ">", "1", "and", "id", "<", "5"))
    val compiled = queryCompiler.compileWhere(columns, recipe)

    assertEquals(false, compiled.isSatisfied(Record().also {
      it.cells.add(RecordCell(INT, "1"))
    }))
    assertEquals(true, compiled.isSatisfied(Record().also {
      it.cells.add(RecordCell(INT, "3"))
    }))
    assertEquals(false, compiled.isSatisfied(Record().also {
      it.cells.add(RecordCell(INT, "5"))
    }))
    assertEquals(false, compiled.isSatisfied(Record().also {
      it.cells.add(RecordCell(INT, "6"))
    }))
  }
}