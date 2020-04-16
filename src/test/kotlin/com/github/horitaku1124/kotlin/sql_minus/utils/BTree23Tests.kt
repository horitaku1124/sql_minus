package com.github.horitaku1124.kotlin.sql_minus.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class BTree23Tests {
  @Test
  fun intComparetor() {
    val compare: (Int, Int) -> Int = { left, right ->  left - right   }

    assertEquals(-1, compare(1,2))
    assertEquals(0, compare(2,2))
    assertEquals(1, compare(3,2))
  }
  @Test
  fun insert2values() {
    val compare: (Int, Int) -> Int = { left, right ->  left - right   }
    var root = BTree23(compare)
    root.insert(10)
    root.insert(13)
    assertEquals(listOf(10, 13), root.values)
    root = BTree23(compare)
    root.insert(3)
    root.insert(2)
    assertEquals(listOf(2, 3), root.values)
  }
  @Test
  fun insertRecursive() {
    val compare: (Int, Int) -> Int = { left, right ->  left - right   }
    val root = BTree23(compare)
    root.insert(10)
    root.insert(13)
    root.insert(5)
    assertEquals(listOf(10, 13), root.values)
    assertNotNull(root.left)
    assertNull( root.middle)
    assertNull(root.right)
    assertEquals(listOf(5), root.left!!.values)

    root.insert(11)
    assertNotNull(root.left)
    assertNotNull(root.middle)
    assertNull(root.right)
    assertEquals(listOf(5), root.left!!.values)
    assertEquals(listOf(11), root.middle!!.values)
  }
  @Test
  fun containsValue() {
    val compare: (Int, Int) -> Int = { left, right ->  left - right   }
    val root = BTree23(compare)
    assertEquals(false, root.contains(10))
    root.insert(10)
    assertEquals(true, root.contains(10))
    root.insert(12)
    assertEquals(true, root.contains(12))
    assertEquals(false, root.contains(11))
    root.insert(9)
    assertEquals(true, root.contains(9))
    root.insert(11)
    assertEquals(true, root.contains(11))
  }
  @Test
  fun findObjValue() {
    val compare: (Int, Int) -> Int = { left, right ->  left - right   }
    val root = BTree23(compare)
    assertEquals(false, root.findObj(10).isPresent)
    root.insert(10)
    assertEquals(true, root.findObj(10).isPresent)
    assertEquals(10, root.findObj(10).get())
    root.insert(12)
    root.insert(9)
    root.insert(11)
    assertEquals(12, root.findObj(12).get())
    assertEquals(9, root.findObj(9).get())
    assertEquals(11, root.findObj(11).get())
  }
}