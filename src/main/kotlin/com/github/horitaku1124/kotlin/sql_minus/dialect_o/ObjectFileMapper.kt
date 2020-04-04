package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import java.io.*

class ObjectFileMapper {
  fun readData(file: File):DatabaseInformation {
    // TODO It is not supported by GraalVM

    return FileInputStream(file).use { fi ->
      ObjectInputStream(fi).use { oi ->
        oi.readObject() as DatabaseInformation
      }
    }
  }

  fun writeData(file: File, dbInfo: DatabaseInformation) {
    // TODO It is not supported by GraalVM

    FileOutputStream(file).use { fo ->
      ObjectOutputStream(fo).use { ow ->
        ow.writeObject(dbInfo)
      }
    }
  }
}