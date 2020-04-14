package com.github.horitaku1124.kotlin.sql_minus.dialect_o.io_mapper

import java.io.File

interface FileMapper<T> {
  fun loadObject(file: File): T
  fun storeObject(file: File, obj: T)
}