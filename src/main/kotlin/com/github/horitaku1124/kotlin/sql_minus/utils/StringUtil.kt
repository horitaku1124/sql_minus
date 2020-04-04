package com.github.horitaku1124.kotlin.sql_minus.utils

object StringUtil {
  fun hash(str: String): String {
    var hash = 0L
    str.toCharArray().forEach { c ->
      hash += c.toInt()
    }
    return String.format("%016x", hash)
  }
}