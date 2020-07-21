package com.github.horitaku1124.kotlin.sql_minus.dialect_o.repositories

import java.io.File

interface SingleFileRepository<T> {
  fun loadObject(file: File): T
  fun storeObject(file: File, obj: T)
}