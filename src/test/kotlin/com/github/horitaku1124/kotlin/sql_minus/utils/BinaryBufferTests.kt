package com.github.horitaku1124.kotlin.sql_minus.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BinaryBufferTests {
  @Test
  fun test1() {
    val bools = Array(1) {true}
    val bb = BinaryBuffer(bools)
    val bytes = bb.toBytes()

    assertEquals(1, bytes.size)
    assertEquals(0b10000000.toByte(), bytes[0])
  }
  @Test
  fun test2() {
    val bools = Array(5) { true }
    bools[2] = false
    bools[4] = false
    val bb = BinaryBuffer(bools)
    val bytes = bb.toBytes()

    assertEquals(1, bytes.size)
    assertEquals(0b11010000.toByte(), bytes[0])
  }
  @Test
  fun test3() {
    val bools = Array(8) { true }
    bools[1] = false
    bools[7] = false
    val bb = BinaryBuffer(bools)
    val bytes = bb.toBytes()

    assertEquals(1, bytes.size)
    assertEquals(0b10111110.toByte(), bytes[0])
  }
  @Test
  fun test4() {
    val bools = Array(9) { true }
    bools[1] = false
    bools[7] = false
    val bb = BinaryBuffer(bools)
    val bytes = bb.toBytes()

    assertEquals(2, bytes.size)
    assertEquals(0b10111110.toByte(), bytes[0])
    assertEquals(0b10000000.toByte(), bytes[1])
  }

  @Test
  fun test5() {
    val bytes = byteArrayOf(0b10000000.toByte())
    val bools = BinaryBuffer.loadFrom(bytes, 1)
    assertEquals(1, bools.size)
    assertEquals(true, bools[0])
  }
  @Test
  fun test6() {
    val bytes = byteArrayOf(0b10100000.toByte())
    val bools = BinaryBuffer.loadFrom(bytes, 4)
    assertEquals(4, bools.size)
    assertEquals(true, bools[0])
    assertEquals(false, bools[1])
    assertEquals(true, bools[2])
    assertEquals(false, bools[3])
  }
  @Test
  fun test7() {
    val bytes = byteArrayOf(
      0b00000000.toByte(),
      0b10000000.toByte()
    )
    val bools = BinaryBuffer.loadFrom(bytes, 10)
    assertEquals(10, bools.size)

    for (i in 0 until 8) {
      assertEquals(false, bools[i])
    }

    assertEquals(true, bools[8])
  }
}