package com.github.horitaku1124.kotlin.sql_minus

import com.github.horitaku1124.kotlin.sql_minus.dialect_o.DatabaseInformation
import java.nio.file.Path
import java.util.*

class ClientSession {
  private var currentDatabase: Optional<String> = Optional.empty()

  fun getCurrentDatabase(): Optional<String>{
    return currentDatabase
  }
  fun setCurrentDatabase(value: String) {
    currentDatabase = Optional.of(value)
  }
  lateinit var dbInfo: DatabaseInformation

  lateinit var dbPath: Path
}