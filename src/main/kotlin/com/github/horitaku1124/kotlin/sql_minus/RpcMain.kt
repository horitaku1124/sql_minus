package com.github.horitaku1124.kotlin.sql_minus

import java.net.ServerSocket

object RpcMain {
  @JvmStatic
  fun main(args: Array<String>) {
    val port = args[0].toInt()
    val listener = ServerSocket(port)

    println("DB.RPC started at $port")
    while (true) {
      println("*start accept")
      val accept = listener.accept()
      println(" connected from " + accept.inetAddress)
      val server = DBRpcServer(accept)
      server.start()
    }
  }
}