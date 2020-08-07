package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.ColumnType
import com.github.horitaku1124.kotlin.sql_minus.QueryParser
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.*
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
  fun createTableCanBeParsed2() {
    val sql = """
      create table tb1 (
          name varchar(20),
          age int,
          status smallint,
          initial char
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

          assertEquals(4, createTable.columns.size)
          createTable.columns[0].let { col ->
            assertEquals("name", col.name)
            assertEquals(ColumnType.VARCHAR, col.type)
            assertEquals(20, col.length)
          }
          createTable.columns[1].let { col ->
            assertEquals("age", col.name)
            assertEquals(ColumnType.INT, col.type)
          }
          createTable.columns[2].let { col ->
            assertEquals("status", col.name)
            assertEquals(ColumnType.SMALLINT, col.type)
          }
          createTable.columns[3].let { col ->
            assertEquals("initial", col.name)
            assertEquals(ColumnType.CHAR, col.type)
            assertEquals(1, col.length)
          }
        }
      }
    }
  }
  @Test
  fun createTableCanBeParsed3() {
    val sql = """
      create table tb1 (
          num1 number(10),
          num2 number(5,3)
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

          assertEquals(2, createTable.columns.size)
          createTable.columns[0].let { col ->
            assertEquals("num1", col.name)
            assertEquals(ColumnType.NUMBER, col.type)
            assertEquals(10, col.numberFormat!!.first)
            assertEquals(0, col.numberFormat!!.second)
          }
          createTable.columns[1].let { col ->
            assertEquals("num2", col.name)
            assertEquals(ColumnType.NUMBER, col.type)
            assertEquals(5, col.numberFormat!!.first)
            assertEquals(3, col.numberFormat!!.second)
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
        assertEquals(123, recipe.records[0].cells[0].intValue)
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
  fun createTable3CanBeParsed() {
    val sql = """
      create table tb3 (
          id int,
          created_at timestamp,
          updated_at date
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
        assertEquals("tb3", syntax.subject)
        val recipe = syntax.recipe.get() as CreateTableRecipe

        assertEquals(3, recipe.columns.size)
        assertEquals("id", recipe.columns[0].name)
        assertEquals("created_at", recipe.columns[1].name)
        assertEquals("updated_at", recipe.columns[2].name)
        assertEquals(ColumnType.INT, recipe.columns[0].type)
        assertEquals(ColumnType.TIMESTAMP, recipe.columns[1].type)
        assertEquals(ColumnType.DATE, recipe.columns[2].type)
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
        assertEquals(123, recipe.records[0].cells[0].intValue)
        assertEquals(ColumnType.VARCHAR, recipe.records[0].cells[1].type)
        assertEquals("abcde", recipe.records[0].cells[1].textValue)
      }
    }
  }

  @Test
  fun insertInto3CanBeParsed() {
    val sql = "insert into tb3(price1,price2) values (123,123.567)"
    val qp = QueryParser()
    val tn = Tokenizer()
    qp.lexicalAnalysis(sql).let { tokens ->
      val st = tn.parse(tokens)
      assertEquals(1, st.size)
      st[0].let { syntax ->
        assertEquals("tb3", syntax.subject)
        val recipe = syntax.recipe.get() as InsertIntoRecipe

        assertEquals(2, recipe.columns.size)
        assertEquals("price1", recipe.columns[0])
        assertEquals("price2", recipe.columns[1])
        assertEquals(1, recipe.records.size)
        assertEquals(2, recipe.records[0].cells.size)
        assertEquals(ColumnType.INT, recipe.records[0].cells[0].type)
        assertEquals(123, recipe.records[0].cells[0].intValue)
        assertEquals(ColumnType.VARCHAR, recipe.records[0].cells[1].type)
        assertEquals("123.567", recipe.records[0].cells[1].textValue)
      }
    }
  }

  @Test
  fun select1CanBeParsed() {
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
  @Test
  fun select2CanBeParsed() {
    val sql = "select id,name from tb3"
    val qp = QueryParser()
    val tn = Tokenizer()
    qp.lexicalAnalysis(sql).let { tokens ->
      println(tokens)
      val st = tn.parse(tokens)
      assertEquals(1, st.size)
      st[0].let { syntax ->
        val recipe = syntax.recipe.get() as SelectQueryRecipe

        assertEquals(2, recipe.selectParts.size)
        assertEquals("id", recipe.selectParts[0])
        assertEquals("name", recipe.selectParts[1])
        assertEquals(1, recipe.fromParts.size)
        assertEquals("tb3", recipe.fromParts[0])
      }
    }
  }
  @Test
  fun select3CanBeParsed() {
    val sql = "select id,name from tb3 where status = 1"
    val qp = QueryParser()
    val tn = Tokenizer()
    qp.lexicalAnalysis(sql).let { tokens ->
      println(tokens)
      val st = tn.parse(tokens)
      assertEquals(1, st.size)
      st[0].let { syntax ->
        val recipe = syntax.recipe.get() as SelectQueryRecipe

        assertEquals(2, recipe.selectParts.size)
        assertEquals("id", recipe.selectParts[0])
        assertEquals("name", recipe.selectParts[1])
        assertEquals(1, recipe.fromParts.size)
        assertEquals("tb3", recipe.fromParts[0])
        assertEquals(3, recipe.whereTree.expression.size)
        assertEquals("status", recipe.whereTree.expression[0])
        assertEquals("=", recipe.whereTree.expression[1])
        assertEquals("1", recipe.whereTree.expression[2])
      }
    }
  }
  @Test
  fun select4CanBeParsed() {
    val sql = "select id,name from tb3 where status = 1 and age > 10"
    val qp = QueryParser()
    val tn = Tokenizer()
    qp.lexicalAnalysis(sql).let { tokens ->
      println(tokens)
      val st = tn.parse(tokens)
      assertEquals(1, st.size)
      st[0].let { syntax ->
        val recipe = syntax.recipe.get() as SelectQueryRecipe

        assertEquals(2, recipe.selectParts.size)
        assertEquals("id", recipe.selectParts[0])
        assertEquals("name", recipe.selectParts[1])
        assertEquals(1, recipe.fromParts.size)
        assertEquals("tb3", recipe.fromParts[0])
        assertEquals(7, recipe.whereTree.expression.size)
        recipe.whereTree.expression.let { exp ->
          assertEquals("status", exp[0])
          assertEquals("=", exp[1])
          assertEquals("1", exp[2])
          assertEquals("and", exp[3])
          assertEquals("age", exp[4])
          assertEquals(">", exp[5])
          assertEquals("10", exp[6])
        }
      }
    }
  }
  @Test
  fun select5CanBeParsed() {
    val sql = "select * from tb3 where name is null"

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
        assertEquals("tb3", recipe.fromParts[0])
        assertEquals(3, recipe.whereTree.expression.size)
        recipe.whereTree.expression.let { exp ->
          assertEquals("name", exp[0])
          assertEquals("is", exp[1])
          assertEquals("null", exp[2])
        }
      }
    }
  }

  @Test
  fun updateCanBeParsed() {
    val sql = "update tb3 set status = 3 where status = 1"
    val qp = QueryParser()
    val tn = Tokenizer()
    qp.lexicalAnalysis(sql).let { tokens ->
      println(tokens)
      val st = tn.parse(tokens)
      assertEquals(1, st.size)

      st[0].let { syntax ->
        val recipe = syntax.recipe.get() as UpdateQueryRecipe
        assertEquals("tb3", recipe.targetTable)
      }
    }
  }
  @Test
  fun deleteCanBeParsed() {
    val sql = "delete from tb1 where status = 1"
    val qp = QueryParser()
    val tn = Tokenizer()
    qp.lexicalAnalysis(sql).let { tokens ->
      println(tokens)
      val st = tn.parse(tokens)
      assertEquals(1, st.size)

      st[0].let { syntax ->
        val recipe = syntax.recipe.get() as DeleteQueryRecipe
        assertEquals("tb1", recipe.targetTable)
      }
    }
  }
}