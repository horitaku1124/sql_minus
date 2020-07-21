package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.dialect_o.journals.TableJournal
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.repositories.TableFileMapper

open class SystemTableFileMapperBuilder {
  fun build(table: TableJournal, tablePath: String): TableFileMapper {
    return TableFileMapper(
      table,
      tablePath
    )
  }
}