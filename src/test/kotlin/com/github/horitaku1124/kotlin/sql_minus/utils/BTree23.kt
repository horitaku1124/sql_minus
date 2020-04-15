package com.github.horitaku1124.kotlin.sql_minus.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class BTree23 {
  class Btree<T>(var comparator: (T,T) -> Int) {

    var values = arrayListOf<T>()

    var left: Btree<T>? = null
    var middle: Btree<T>? = null
    var right: Btree<T>? = null

    fun insert(value: T) {
      if (values.size == 0) {
        values.add(value)
        return
      }
      if (values.size == 1) {
        var diff = comparator(values[0], value)
        if (diff < 0) {
          values.add(value)
        } else if (diff > 0) {
          values.add(0, value)
        }
        return
      }
      if (values.size == 2) {
        var diff = comparator(values[0], value)
        if (diff > 0) {
          if (left == null) {
            left = Btree(comparator)
          }
          left!!.insert(value)
          return
        } else if (diff == 0) {
          return
        }
        var diff2 = comparator(value, values[1])
        if (diff2 < 0) {
          if (middle == null) {
            middle = Btree(comparator)
          }
          middle!!.insert(value)
          return
        }
        if (diff2 == 0) {
          return
        }
        if (right == null) {
          right = Btree(comparator)
        }
        right!!.insert(value)
      }
    }
    fun contains(value: T): Boolean {
      if (values.size == 0) return false
      if (values.size == 1) {
        return comparator(value, values[0]) == 0
      }
      if (values.contains(value)) {
        return true
      }
      if (left != null) {
        if (left!!.contains(value)) {
          return true
        }
      }
      if (middle != null) {
        if (middle!!.contains(value)) {
          return true
        }
      }
      if (right != null) {
        if (right!!.contains(value)) {
          return true
        }
      }
      return false
    }
  }
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
    var treeBase = Btree(compare)
    treeBase.insert(10)
    treeBase.insert(13)
    assertEquals(listOf(10, 13), treeBase.values)
    treeBase = Btree(compare)
    treeBase.insert(3)
    treeBase.insert(2)
    assertEquals(listOf(2, 3), treeBase.values)
  }
  @Test
  fun insertRecursive() {
    val compare: (Int, Int) -> Int = { left, right ->  left - right   }
    var treeBase = Btree(compare)
    treeBase.insert(10)
    treeBase.insert(13)
    treeBase.insert(5)
    assertEquals(listOf(10, 13), treeBase.values)
    assertNotNull(treeBase.left)
    assertNull( treeBase.middle)
    assertNull(treeBase.right)
    assertEquals(listOf(5), treeBase.left!!.values)

    treeBase.insert(11)
    assertNotNull(treeBase.left)
    assertNotNull(treeBase.middle)
    assertNull(treeBase.right)
    assertEquals(listOf(5), treeBase.left!!.values)
    assertEquals(listOf(11), treeBase.middle!!.values)
  }
  @Test
  fun containsValue() {
    val compare: (Int, Int) -> Int = { left, right ->  left - right   }
    val treeBase = Btree(compare)
    assertEquals(false, treeBase.contains(10))
    treeBase.insert(10)
    assertEquals(true, treeBase.contains(10))
    treeBase.insert(12)
    assertEquals(true, treeBase.contains(12))
    assertEquals(false, treeBase.contains(11))
    treeBase.insert(9)
    assertEquals(true, treeBase.contains(9))
    treeBase.insert(11)
    assertEquals(true, treeBase.contains(11))
  }
}