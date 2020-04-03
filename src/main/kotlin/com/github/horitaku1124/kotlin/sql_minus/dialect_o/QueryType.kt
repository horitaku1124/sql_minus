package com.github.horitaku1124.kotlin.sql_minus.dialect_o

enum class QueryType {
  CREATE_DATABASE,
  CHANGE_DATABASE,
  CREATE_TABLE,
  ALTER_TABLE,
  DROP_TABLE,
  SELECT_QUERY,
  INSERT_QUERY,
  UPDATE_QUERY,
  DELETE_QUERY,
}