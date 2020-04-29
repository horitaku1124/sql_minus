package com.github.horitaku1124.kotlin.sql_minus.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BinaryBufferTests {
  @Test
  fun test1() {
    var bools = Array(1) {true}
    var bb = BinaryBuffer(bools)
    var bytes = bb.toBytes()

    assertEquals(1, bytes.size)
    assertEquals(0b10000000.toByte(), bytes[0])
  }
  @Test
  fun test2() {
    var bools = Array(5) { true }
    bools[2] = false
    bools[4] = false
    var bb = BinaryBuffer(bools)
    var bytes = bb.toBytes()

    assertEquals(1, bytes.size)
    assertEquals(0b11010000.toByte(), bytes[0])
  }
  @Test
  fun test3() {
    var bools = Array(8) { true }
    bools[1] = false
    bools[7] = false
    var bb = BinaryBuffer(bools)
    var bytes = bb.toBytes()

    assertEquals(1, bytes.size)
    assertEquals(0b10111110.toByte(), bytes[0])
  }
  @Test
  fun test4() {
    var bools = Array(9) { true }
    bools[1] = false
    bools[7] = false
    var bb = BinaryBuffer(bools)
    var bytes = bb.toBytes()

    assertEquals(2, bytes.size)
    assertEquals(0b10111110.toByte(), bytes[0])
    assertEquals(0b10000000.toByte(), bytes[1])
  }
}