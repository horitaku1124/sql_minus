package com.github.horitaku1124.kotlin.sql_minus.utils

class BinaryBuffer(private var bools: Array<Boolean>) {
  companion object {
    fun octetOf(n: Int): Int {
      return (n - 1) / 8 + 1
    }
  }

  fun toBytes(): ByteArray {
    val octect = octetOf(bools.size)

    val buff = Array<Byte>(octect){0}
    var index = 0
    for (i in 0 until octect) {
      var bit = 0
      for (j in 0 until 8) {
        bit = bit shl 1
        if (index < bools.size && bools[index]) {
          bit = bit or 1
        }
        index++
      }
      buff[i] = bit.toByte()
    }
    return buff.toByteArray()
  }
}