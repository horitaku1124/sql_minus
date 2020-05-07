package com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WhereVerifyGateTests {

  @Test
  fun isIntTest() {
    val isIntReg = "^-?\\d+$".toRegex()
    assertEquals(true, isIntReg.matches("1"))
    assertEquals(true, isIntReg.matches("123456789"))
    assertEquals(true, isIntReg.matches("-100"))
    assertEquals(false, isIntReg.matches("a"))
    assertEquals(false, isIntReg.matches("123b"))
    assertEquals(false, isIntReg.matches("b123"))
  }
}