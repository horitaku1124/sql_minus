package com.github.horitaku1124.kotlin.sql_minus

import java.lang.RuntimeException
import java.net.Socket

class DataBaseServer(private var socket: Socket): java.lang.Thread() {
  companion object {
    private lateinit var dbEngine: DatabaseEngine
    init {
      println("DataBaseServer.init()")
      dbEngine = DatabaseEngine()
    }
  }
  override fun run() {
    val buf = ByteArray(1024 * 1024)
    var fromClient = socket.getInputStream()
    var toLClient = socket.getOutputStream()

    var queryParser = QueryParser()
    var tokenizer = Tokenizer()
    while(true) {
      toLClient.write("DB>".toByteArray())
      var len = fromClient.read(buf)
      if (len < 0) break

      println("< " + len)

      var query = String(buf, 0, len)
      var tokens = queryParser.lexicalAnalysis(query)
      var syntaxList = tokenizer.parse(tokens)

      try {
        syntaxList.forEach {syntax ->
          dbEngine.execute(syntax)
        }
      } catch (e: RuntimeException) {
        toLClient.write(("ERROR:" + e.message!! + "\n").toByteArray())
      }
    }

    toLClient.close()
    fromClient.close()
  }
}