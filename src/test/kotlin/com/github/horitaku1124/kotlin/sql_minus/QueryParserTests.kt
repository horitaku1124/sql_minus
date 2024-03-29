package com.github.horitaku1124.kotlin.sql_minus

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class QueryParserTests {
  @Test
  fun basicQueryCanBeParsed() {
    val qp = QueryParser()

    qp.lexicalAnalysisAndLigature("select * from table").let { tokens ->
      assertEquals(4, tokens.size)
      assertEquals("select", tokens[0])
      assertEquals("*", tokens[1])
      assertEquals("from", tokens[2])
      assertEquals("table", tokens[3])
    }
    qp.lexicalAnalysisAndLigature("insert into table").let { tokens ->
      assertEquals(3, tokens.size)
      assertEquals("insert", tokens[0])
      assertEquals("into", tokens[1])
      assertEquals("table", tokens[2])
    }
    qp.lexicalAnalysisAndLigature("update table set").let { tokens ->
      assertEquals(3, tokens.size)
      assertEquals("update", tokens[0])
      assertEquals("table", tokens[1])
      assertEquals("set", tokens[2])
    }
    qp.lexicalAnalysisAndLigature("delete from table").let { tokens ->
      assertEquals(3, tokens.size)
      assertEquals("delete", tokens[0])
      assertEquals("from", tokens[1])
      assertEquals("table", tokens[2])
    }
  }
  @Test
  fun queryWithSymbolsIsParsed() {
    val qp = QueryParser()
    qp.lexicalAnalysisAndLigature("select a,b,c from table where d = 1 and e=2 and fgh<34").let { tokens ->
      assertEquals(20, tokens.size)
      assertEquals("select", tokens[0])
      assertEquals("a", tokens[1])
      assertEquals(",", tokens[2])
      assertEquals("b", tokens[3])
      assertEquals(",", tokens[4])
      assertEquals("c", tokens[5])
      assertEquals("from", tokens[6])
      assertEquals("table", tokens[7])
      assertEquals("where", tokens[8])
      assertEquals("d", tokens[9])
      assertEquals("=", tokens[10])
      assertEquals("1", tokens[11])
      assertEquals("and", tokens[12])
      assertEquals("e", tokens[13])
      assertEquals("=", tokens[14])
      assertEquals("2", tokens[15])
      assertEquals("and", tokens[16])
      assertEquals("fgh", tokens[17])
      assertEquals("<", tokens[18])
      assertEquals("34", tokens[19])
    }
  }
  @Test
  fun selectQuery2() {
    val qp = QueryParser()
    qp.lexicalAnalysisAndLigature("select * from t1 where s<>1 and (a=2 or a<3)").let { tokens ->
      assertEquals(18, tokens.size)
      assertEquals("select", tokens[0])
      assertEquals("*", tokens[1])
      assertEquals("from", tokens[2])
      assertEquals("t1", tokens[3])
      assertEquals("where", tokens[4])
      assertEquals("s", tokens[5])
      assertEquals("<>", tokens[6])
      assertEquals("1", tokens[7])
      assertEquals("and", tokens[8])
      assertEquals("(", tokens[9])
      assertEquals("a", tokens[10])
      assertEquals("=", tokens[11])
      assertEquals("2", tokens[12])
      assertEquals("or", tokens[13])
      assertEquals("a", tokens[14])
      assertEquals("<", tokens[15])
      assertEquals("3", tokens[16])
      assertEquals(")", tokens[17])
    }
  }
  @Test
  fun basicInsertCanBeParsed() {
    val qp = QueryParser()
    qp.lexicalAnalysisAndLigature("insert into table1(val1,val2,val3) values ('123','456','789')").let { tokens ->
      assertEquals(18, tokens.size)
      assertEquals("insert", tokens[0])
      assertEquals("into", tokens[1])
      assertEquals("table1", tokens[2])
      assertEquals("(", tokens[3])
      assertEquals("val1", tokens[4])
      assertEquals(",", tokens[5])
      assertEquals("val2", tokens[6])
      assertEquals(",", tokens[7])
      assertEquals("val3", tokens[8])
      assertEquals(")", tokens[9])
      assertEquals("values", tokens[10])
      assertEquals("(", tokens[11])
      assertEquals("'123'", tokens[12])
      assertEquals(",", tokens[13])
      assertEquals("'456'", tokens[14])
      assertEquals(",", tokens[15])
      assertEquals("'789'", tokens[16])
      assertEquals(")", tokens[17])
    }
  }
  @Test
  fun basicCreateCanBeParsed() {
    val qp = QueryParser()

    qp.lexicalAnalysisAndLigature("create table table1 (\n" +
        "    id int auto_increment\n" +
        "        primary key,\n" +
        "    name varchar(30) null\n" +
        ");").let { tokens ->
      println(tokens)
      assertEquals(18, tokens.size)
      assertEquals("create", tokens[0])
      assertEquals("table", tokens[1])
      assertEquals("table1", tokens[2])
      assertEquals("(", tokens[3])
      assertEquals("id", tokens[4])
      assertEquals("int", tokens[5])
      assertEquals("auto_increment", tokens[6])
      assertEquals("primary", tokens[7])
      assertEquals("key", tokens[8])
      assertEquals(",", tokens[9])
      assertEquals("name", tokens[10])
      assertEquals("varchar", tokens[11])
      assertEquals("(", tokens[12])
      assertEquals("30", tokens[13])
      assertEquals(")", tokens[14])
      assertEquals("null", tokens[15])
      assertEquals(")", tokens[16])
      assertEquals(";", tokens[17])
    }
  }
}