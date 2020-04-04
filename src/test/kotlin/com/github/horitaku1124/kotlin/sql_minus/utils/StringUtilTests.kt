package com.github.horitaku1124.kotlin.sql_minus.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class StringUtilTests {
  @Test
  fun hashTest() {
    val hash1 = StringUtil.hash("a")
    val hash2 = StringUtil.hash("A")

    assertNotEquals(hash1, hash2)
    val hash3 = StringUtil.hash(StringBuffer().append("a").toString())

    assertEquals(hash1, hash3)
  }
}