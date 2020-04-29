package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TableFileMapperTests {
  private fun octetOf(n: Int): Int {
    return (n - 1) / 8 + 1
  }
  @Test
  fun octetCount() {
    assertEquals(1, octetOf(1))
    assertEquals(1, octetOf(2))
    assertEquals(1, octetOf(7))
    assertEquals(1, octetOf(8))
    assertEquals(2, octetOf(9))
    assertEquals(2, octetOf(16))
    assertEquals(3, octetOf(17))
    assertEquals(4, octetOf(32))
    assertEquals(128, octetOf(1024))
  }
}