package com.github.horitaku1124.kotlin.sql_minus

import java.util.*

class ClientSession {
  private var currentDatabase: Optional<String> = Optional.empty()

  fun getCurrentDatabase(): Optional<String>{
    return currentDatabase
  }
  fun setCurrentDatabase(value: String) {
    currentDatabase = Optional.of(value)
  }
}