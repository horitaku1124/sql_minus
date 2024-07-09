package com.github.horitaku1124.kotlin.sql_minus

import com.fasterxml.jackson.databind.ObjectMapper
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
      val newBuf = buf.copyOf(len)

      println("< " + len)
      val parseFrom = ExecQueryProtos.Query.parseFrom(newBuf)

      println("query2=" + parseFrom.query)

      try {
        val tokens = queryParser.lexicalAnalysisAndLigature(parseFrom.query)
        val syntaxList = tokenizer.parse(tokens)

        syntaxList.forEach {syntax ->
          val result = dbEngine.execute(syntax, session)

          val body = if (result.queryType != QueryType.SELECT_QUERY) result.message else {
            val mapper = ObjectMapper()
            mapper.writeValueAsString(result.resultData)
          }
          val resultProtos = ExecResultProtos.Result.newBuilder()
            .setStatus(if (result.status == ExecuteResult.ResultStatus.OK) 1 else 2)
            .setBody(body)
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
