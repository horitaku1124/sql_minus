package com.github.horitaku1124.kotlin.sql_minus.utils

class BinaryBuffer(private var bools: Array<Boolean>) {
  companion object {
    fun octetOf(n: Int): Int {
      return (n - 1) / 8 + 1
    }

    fun loadFrom(buff: ByteArray, columnNum: Int):Array<Boolean>  {
      val bools = Array(columnNum) {false}
      var index = 0
      for (i in buff.indices) {
        val byte = buff[i].toInt().and(255)
        var mask = 0b1000_0000
        for (j in 0 until 8) {
          if (index < columnNum) {
            bools[index] = (byte.and(mask)) != 0
          }

          index++
          mask = mask shr 1
        }
      }
      return bools
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