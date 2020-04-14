package com.github.horitaku1124.kotlin.sql_minus.dialect_o.io_mapper

import com.github.horitaku1124.kotlin.sql_minus.dialect_o.DatabaseInformation
import java.io.*

class JavaObjectMapper: FileMapper<DatabaseInformation> {
  override fun loadObject(file: File): DatabaseInformation {
    return FileInputStream(file).use { fi ->
      ObjectInputStream(fi).use { oi ->
        oi.readObject() as DatabaseInformation
      }
    }
  }

  override fun storeObject(file: File, obj: DatabaseInformation) {
    FileOutputStream(file).use { fo ->
      ObjectOutputStream(fo).use { ow ->
        ow.writeObject(obj)
      }
    }
  }
}