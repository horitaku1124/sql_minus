package com.github.horitaku1124.kotlin.sql_minus

import com.github.horitaku1124.kotlin.sql_minus.dialect_o.DatabaseEngine
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.SystemTableFileMapperBuilder
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.Tokenizer
import com.github.horitaku1124.kotlin.sql_minus_driver.protos.ExecQueryProtos
import com.github.horitaku1124.kotlin.sql_minus_driver.protos.ExecResultProtos
import java.net.Socket

class DBRpcServer(private var socket: Socket): java.lang.Thread() {
  companion object {
    private var dbEngine: DatabaseEngine
    init {
      println("DataBaseServer.init()")
      dbEngine = DatabaseEngine(SystemTableFileMapperBuilder())
    }
  }
  override fun run() {
    val buf = ByteArray(1024 * 1024)
    val fromClient = socket.getInputStream()
    val toLClient = socket.getOutputStream()


    val queryParser = QueryParser()
    val tokenizer = Tokenizer()
    val session = ClientSession()
    while(socket.isConnected && !socket.isClosed) {
      val len = fromClient.read(buf)
      if (len < 0) break
      var newBuf = buf.copyOf(len)

      println("< " + len)
      val parseFrom = ExecQueryProtos.Query.parseFrom(newBuf)

      println("query2=" + parseFrom.query)

      try {
        val tokens = queryParser.lexicalAnalysis(parseFrom.query)
        val syntaxList = tokenizer.parse(tokens)

        val sb = StringBuffer()
        syntaxList.forEach {syntax ->
          val result = dbEngine.execute(syntax, session)
          sb.append(result)
        }
        val resultProtos = ExecResultProtos.Result.newBuilder()
          .setStatus(1)
          .setBody(sb.toString())
          .build()
        val resultBytes = resultProtos.toByteArray()
        toLClient.write(resultBytes)
        toLClient.flush()
      } catch (e: DBRuntimeException) {
        e.printStackTrace()

        val query = ExecResultProtos.Result.newBuilder()
          .setStatus(2)
          .setBody("ERROR:" + e.message)
          .build()
        query.writeTo(toLClient)
      }
    }

    toLClient.close()
    fromClient.close()
    println("socket closed")
  }
}