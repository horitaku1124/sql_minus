package com.github.horitaku1124.kotlin.sql_minus

import com.github.horitaku1124.kotlin.sql_minus.dialect_o.*
import java.net.Socket

class DataBaseServer(private var socket: Socket): Thread() {
  companion object {
    private var dbEngine2: DatabaseEngineCore
    init {
      println("DataBaseServer.init()")
      dbEngine2 = DatabaseEngineCore(SystemTableFileMapperBuilder())
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

      println("< $len")

      val query = String(buf, 0, len)
      if (query.trim() == "exit") {
        strToClient("Bye.\n")
        break
      }

      try {
        val tokens = queryParser.lexicalAnalysisAndLigature(query)
        val syntaxList = tokenizer.parse(tokens)

        syntaxList.forEach {syntax ->
          val startedAt = System.currentTimeMillis()
          val result2 = dbEngine2.execute(syntax, session)

          if (result2.status == ExecuteResult.ResultStatus.OK) {
            strToClient(prettyPrint(syntax, result2))

            val endedAt = System.currentTimeMillis()
            strToClient((endedAt - startedAt).toString() + "ms\n")
          } else {
            strToClient("Error:${result2.message}")
          }
        }
      } catch (e: DBRuntimeException) {
//        e.printStackTrace()
        strToClient("ERROR:" + e.message!! + "\n")
      }
    }

    toLClient.close()
    fromClient.close()
  }

  private fun prettyPrint(syntax: QueryRecipe, result: ExecuteResult): String {
    if (syntax.type == QueryType.SELECT_QUERY) {
      val str = StringBuffer()
      if (result.resultData.isNotEmpty()) {
        val keys = result.resultData[0].keys.toList()
        str.append(keys.joinToString("\t")).append("\n")
        str.append("-".repeat(20)).append("\n")
        for (record in result.resultData) {
          for (i in keys) {
            val cell = record[i]
            if (cell == null) {
              str.append("NULL")
            } else {
              str.append(cell)
            }
            str.append('\t')
          }
          str.append('\n')
        }
      }
      return result.message + "\n" + str.toString()
    } else {
      return result.message
    }
  }
}