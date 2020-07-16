package com.github.horitaku1124.kotlin.sql_minus.dialect_o

class ExecuteResult {
  enum class ResultStatus {
    OK,
    ERROR,
  }

  lateinit var status: ResultStatus
  lateinit var message: String
  lateinit var queryType: QueryType

  object ExecuteResultBuilder {
    fun builder(): ExecuteResultObject{
      return ExecuteResultObject()
    }

    fun errorCase(): ExecuteResult{
      return ExecuteResultObject()
        .setQueryType(QueryType.UNKNOWN)
        .setStatus(ResultStatus.ERROR)
        .build()
    }
  }
  class ExecuteResultObject {
    private var status: ResultStatus? = null
    private var message: String? = null
    private var queryType: QueryType? = null
    fun setStatus(status: ResultStatus): ExecuteResultObject{
      this.status = status
      return this
    }
    fun setMessage(message: String): ExecuteResultObject{
      this.message = message
      return this
    }
    fun setQueryType(queryType: QueryType): ExecuteResultObject{
      this.queryType = queryType
      return this
    }

    fun build(): ExecuteResult{
      var result = ExecuteResult()
      if (status != null) {
        result.status = status!!
      }
      if (message != null) {
        result.message = message!!
      }
      if (queryType != null) {
        result.queryType = queryType!!
      }
      return result
    }
  }
}