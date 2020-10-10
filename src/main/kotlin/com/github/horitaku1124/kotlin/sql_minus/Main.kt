package com.github.horitaku1124.kotlin.sql_minus

import java.net.ServerSocket

object Main {
  @JvmStatic
  fun main(args: Array<String>) {
    val port = args[0].toInt()
    val listener = ServerSocket(port)

    println("DB started at $port")
    while (true) {
      println("*start accept")
      val accept = listener.accept()
      println(" connected from " + accept.inetAddress)
      val server = DataBaseServer(accept)
      server.start()
    }
  }
}