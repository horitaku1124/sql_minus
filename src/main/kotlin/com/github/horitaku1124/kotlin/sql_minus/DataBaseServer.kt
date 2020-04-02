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
    val fromClient = socket.getInputStream()
    val toLClient = socket.getOutputStream()
    val strToClient = { str:String ->
      toLClient.write(str.toByteArray())
    }

    val queryParser = QueryParser()
    val tokenizer = Tokenizer()
    val session = ClientSession()
    while(true) {

      strToClient(session.getCurrentDatabase().orElse("*") + ">")
      val len = fromClient.read(buf)
      if (len < 0) break

      println("< " + len)

      val query = String(buf, 0, len)
      if (query.trim() == "exit") {
        strToClient("Bye.\n")
        break
      }

      try {
        val tokens = queryParser.lexicalAnalysis(query)
        val syntaxList = tokenizer.parse(tokens)

        syntaxList.forEach {syntax ->
          dbEngine.execute(syntax, session)
        }
      } catch (e: RuntimeException) {
        strToClient("ERROR:" + e.message!! + "\n")
      }
    }

    toLClient.close()
    fromClient.close()
  }
}