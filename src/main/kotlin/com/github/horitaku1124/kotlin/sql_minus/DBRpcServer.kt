package com.github.horitaku1124.kotlin.sql_minus

import com.github.horitaku1124.kotlin.sql_minus.dialect_o.*
import com.github.horitaku1124.kotlin.sql_minus_driver.protos.ExecQueryProtos
import com.github.horitaku1124.kotlin.sql_minus_driver.protos.ExecResultProtos
import java.net.Socket

class DBRpcServer(private var socket: Socket): Thread() {
  companion object {
    private var dbEngine: DatabaseEngineCore
    init {
      println("DataBaseServer.init()")
      dbEngine = DatabaseEngineCore(SystemTableFileMapperBuilder())
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
        val tokens = queryParser.lexicalAnalysisAndLigature(parseFrom.query)
        val syntaxList = tokenizer.parse(tokens)

        syntaxList.forEach {syntax ->
          val result = dbEngine.execute(syntax, session)

          val resultProtos = ExecResultProtos.Result.newBuilder()
            .setStatus(if (result.status == ExecuteResult.ResultStatus.OK) 1 else 2)
            .setBody(result.message)
            .build()
          val resultBytes = resultProtos.toByteArray()
          toLClient.write(resultBytes)
          toLClient.flush()

        }

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