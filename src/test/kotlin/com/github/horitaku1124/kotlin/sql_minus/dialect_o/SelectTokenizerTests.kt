package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.ast.NodeType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SelectTokenizerTests {
  @Test
  fun select1CanBeParsed1() {
    val tokens = listOf("select", "*", "from", "table1")
    val tn = SelectTokenizer()
    val (selectRecipe, i) = tn.parseSelect(tokens, 0)

    assertTrue(selectRecipe.syntaxTree.isPresent)
    val tree = selectRecipe.syntaxTree.get()
    assertEquals(NodeType.MAP, tree.type)
    assertTrue(tree.property.containsKey("select"))
    assertTrue(tree.property.containsKey("from"))
    tree.property["select"]!!.let { select ->
      assertEquals(1, select.size)
      assertEquals("*", select[0].value)
    }
    tree.property["from"]!!.let { from ->
      assertEquals(1, from.size)
      assertEquals("table1", from[0].value)
    }
  }
  @Test
  fun select2CanBeParsed1() {
    val tokens = listOf("select", "col1", ",", "col2", "from", "table1", ",", "table2")
    val tn = SelectTokenizer()
    val (selectRecipe, i) = tn.parseSelect(tokens, 0)

    assertTrue(selectRecipe.syntaxTree.isPresent)
    val tree = selectRecipe.syntaxTree.get()
    assertEquals(NodeType.MAP, tree.type)
    assertTrue(tree.property.containsKey("select"))
    assertTrue(tree.property.containsKey("from"))
    tree.property["select"]!!.let { select ->
      assertEquals(2, select.size)
      assertEquals("col1", select[0].value)
      assertEquals("col2", select[1].value)
    }
    tree.property["from"]!!.let { from ->
      assertEquals(2, from.size)
      assertEquals("table1", from[0].value)
      assertEquals("table2", from[1].value)
    }
  }
  @Test
  fun select3CanBeParsed1() {
    val tokens = listOf(
      "select",
      "col1", ",", "col2", ",", "table1.col3",
      "from",
      "table1", ",", "table2", ",", "table3"
    )
    val tn = SelectTokenizer()
    val (selectRecipe, i) = tn.parseSelect(tokens, 0)

    assertTrue(selectRecipe.syntaxTree.isPresent)
    val tree = selectRecipe.syntaxTree.get()
    assertEquals(NodeType.MAP, tree.type)
    assertTrue(tree.property.containsKey("select"))
    assertTrue(tree.property.containsKey("from"))
    tree.property["select"]!!.let { select ->
      assertEquals(3, select.size)
      assertEquals("col1", select[0].value)
      assertEquals("col2", select[1].value)
      assertEquals("table1.col3", select[2].value)
    }
    tree.property["from"]!!.let { from ->
      assertEquals(3, from.size)
      assertEquals("table1", from[0].value)
      assertEquals("table2", from[1].value)
      assertEquals("table3", from[2].value)
    }
  }
}