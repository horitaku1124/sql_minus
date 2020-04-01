package com.github.horitaku1124.kotlin.sql_minus

import java.net.ServerSocket

class Main {
  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      val port = args[0].toInt()
      val dbServer = ServerSocket(port)

      println("DB started at $port")
      while (true) {
        println("*start accept")
        val accept = dbServer.accept()
        val server = DataBaseServer(accept)
        server.start()
      }
    }
  }
}