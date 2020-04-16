package com.github.horitaku1124.kotlin.sql_minus.utils

import java.util.*

class BTree23<T> (var comparator: (T, T) -> Int) {
  var values = arrayListOf<T>()

  var left: BTree23<T>? = null
  var middle: BTree23<T>? = null
  var right: BTree23<T>? = null

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
          left = BTree23(comparator)
        }
        left!!.insert(value)
        return
      } else if (diff == 0) {
        return
      }
      var diff2 = comparator(value, values[1])
      if (diff2 < 0) {
        if (middle == null) {
          middle = BTree23(comparator)
        }
        middle!!.insert(value)
        return
      }
      if (diff2 == 0) {
        return
      }
      if (right == null) {
        right = BTree23(comparator)
      }
      right!!.insert(value)
    }
  }
  fun contains(value: T): Boolean {
    if (values.size == 0) return false
    if (values.size == 1) {
      return comparator(value, values[0]) == 0
    }
    for (value1 in values) {
      if (comparator(value, value1) == 0) {
        return true
      }
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
  fun findObj(value: T): Optional<T> {
    if (values.size == 0) return Optional.empty()
    if (values.size == 1) {
      if(comparator(value, values[0]) == 0) {
        return Optional.of(values[0])
      } else {
        return Optional.empty()
      }
    }
    for (value1 in values) {
      if (comparator(value, value1) == 0) {
        return Optional.of(value1)
      }
    }
    if (left != null) {
      val find = left!!.findObj(value)
      if (find.isPresent) {
        return find
      }
    }
    if (middle != null) {
      val find = middle!!.findObj(value)
      if (find.isPresent) {
        return find
      }
    }
    if (right != null) {
      val find = right!!.findObj(value)
      if (find.isPresent) {
        return find
      }
    }
    return Optional.empty()
  }
}