package com.github.horitaku1124.kotlin.sql_minus

import java.net.Socket

class DataBaseServer(private var socket: Socket): java.lang.Thread() {
  override fun run() {
    var fromClient = socket.getInputStream()
    var toLClient = socket.getOutputStream()

    toLClient.write("DB>".toByteArray())

    val buf = ByteArray(1024 * 1024)

    var queryParser = QueryParser()
    while(true) {

      var len = fromClient.read(buf)
      if (len < 0) break

      var query = String(buf, 0, len)
      queryParser.lexicalAnalysis(query)
    }

    toLClient.close()
    fromClient.close()
  }
}