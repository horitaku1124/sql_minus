package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.ColumnType
import com.github.horitaku1124.kotlin.sql_minus.QueryParser
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.CreateTableRecipe
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.InsertIntoRecipe
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.SelectQueryRecipe
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TokenizerTests {
  @Test
  fun createDatabaseCanBeParsed() {
    val qp = QueryParser()
    val tn = Tokenizer()

    qp.lexicalAnalysis("create database db1;").let { tokens ->
      val st = tn.parse(tokens)
      assertEquals(1, st.size)

      st[0].let { syntax ->
        assertEquals(QueryType.CREATE_DATABASE, syntax.type)
        assertEquals("db1", syntax.subject)
      }
    }
  }
  @Test
  fun connectCanBeParsed() {
    val qp = QueryParser()
    val tn = Tokenizer()
    qp.lexicalAnalysis("connect db2").let { tokens ->
      val st = tn.parse(tokens)
      assertEquals(1, st.size)

      st[0].let { syntax ->
        assertEquals(QueryType.CHANGE_DATABASE, syntax.type)
        assertEquals("db2", syntax.subject)
      }
    }
  }
  @Test
  fun createTableCanBeParsed() {
    val sql = """
      create table tb1 (
          id int
      )
    """.trimIndent()
    val qp = QueryParser()
    val tn = Tokenizer()
    qp.lexicalAnalysis(sql).let { tokens ->
      println(tokens)
      val st = tn.parse(tokens)
      println(st)
      assertEquals(1, st.size)

      st[0].let { syntax ->
        assertEquals(QueryType.CREATE_TABLE, syntax.type)
        assertEquals("tb1", syntax.subject)
        syntax.recipe.let { recipe ->
          val createTable = recipe.get() as CreateTableRecipe

          assertEquals(1, createTable.columns.size)
          createTable.columns[0].let { idColumns ->
            assertEquals("id", idColumns.name)
            assertEquals(ColumnType.INT, idColumns.type)
          }
        }
      }
    }
  }
  @Test
  fun insertIntoCanBeParsed() {
    val sql = "insert into tb1(id) values (123)"

    val qp = QueryParser()
    val tn = Tokenizer()
    qp.lexicalAnalysis(sql).let { tokens ->
      println(tokens)
      val st = tn.parse(tokens)

      assertEquals(1, st.size)
      st[0].let { syntax ->
        assertEquals("tb1", syntax.subject)
        val recipe = syntax.recipe.get() as InsertIntoRecipe

        assertEquals(1, recipe.columns.size)
        assertEquals("id", recipe.columns[0])
        assertEquals(1, recipe.records.size)
        assertEquals(1, recipe.records[0].cells.size)
        assertEquals(ColumnType.INT, recipe.records[0].cells[0].type)
        assertEquals("123", recipe.records[0].cells[0].value)
      }
    }
  }
  @Test
  fun createTable2CanBeParsed() {
    val sql = """
      create table tb2 (
          id int,
          name varchar(20)
      )
    """.trimIndent()

    val qp = QueryParser()
    val tn = Tokenizer()
    qp.lexicalAnalysis(sql).let { tokens ->
      println(tokens)
      val st = tn.parse(tokens)

      assertEquals(1, st.size)
      st[0].let { syntax ->
        assertEquals(QueryType.CREATE_TABLE, syntax.type)
        assertEquals("tb2", syntax.subject)
        val recipe = syntax.recipe.get() as CreateTableRecipe

        assertEquals(2, recipe.columns.size)
        assertEquals("id", recipe.columns[0].name)
        assertEquals("name", recipe.columns[1].name)
        assertEquals(ColumnType.INT, recipe.columns[0].type)
        assertEquals(ColumnType.VARCHAR, recipe.columns[1].type)
        assertEquals(20, recipe.columns[1].length!!)
      }
    }
  }
  @Test
  fun insertInto2CanBeParsed() {
    val sql = "insert into tb2(id, name) values (123, 'abcde')"

    val qp = QueryParser()
    val tn = Tokenizer()
    qp.lexicalAnalysis(sql).let { tokens ->
      println(tokens)
      val st = tn.parse(tokens)

      assertEquals(1, st.size)
      st[0].let { syntax ->
        assertEquals("tb2", syntax.subject)
        val recipe = syntax.recipe.get() as InsertIntoRecipe

        assertEquals(2, recipe.columns.size)
        assertEquals("id", recipe.columns[0])
        assertEquals("name", recipe.columns[1])
        assertEquals(1, recipe.records.size)
        assertEquals(2, recipe.records[0].cells.size)
        assertEquals(ColumnType.INT, recipe.records[0].cells[0].type)
        assertEquals("123", recipe.records[0].cells[0].value)
        assertEquals(ColumnType.VARCHAR, recipe.records[0].cells[1].type)
        assertEquals("abcde", recipe.records[0].cells[1].value)
      }
    }
  }
  @Test
  fun selectCanBeParsed() {
    val sql = "select * from tb2"
    val qp = QueryParser()
    val tn = Tokenizer()
    qp.lexicalAnalysis(sql).let { tokens ->
      println(tokens)
      val st = tn.parse(tokens)
      assertEquals(1, st.size)
      st[0].let { syntax ->
        val recipe = syntax.recipe.get() as SelectQueryRecipe

        assertEquals(1, recipe.selectParts.size)
        assertEquals("*", recipe.selectParts[0])
        assertEquals(1, recipe.fromParts.size)
        assertEquals("tb2", recipe.fromParts[0])
      }
    }
  }
}