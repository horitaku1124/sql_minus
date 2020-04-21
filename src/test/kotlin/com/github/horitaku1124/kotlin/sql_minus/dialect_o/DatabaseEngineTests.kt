package com.github.horitaku1124.kotlin.sql_minus.dialect_o

import com.github.horitaku1124.kotlin.sql_minus.ClientSession
import com.github.horitaku1124.kotlin.sql_minus.SyntaxTree
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.journals.TableJournal
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes.SelectQueryRecipe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

@Disabled
class DatabaseEngineTests {
  lateinit var engine: DatabaseEngine
  lateinit var mock: TableFileMapper
  private var dbPath = Path.of("./junit")
  @BeforeEach
  fun before() {
    mock = Mockito.mock(TableFileMapper::class.java)

    engine = DatabaseEngine(object: SystemTableFileMapperBuilder() {
      fun build(): TableFileMapper {
        return mock
      }
    })
    dbPath = Files.createTempDirectory("junit")
    val dummy = dbPath.resolve("tb1.dummy")
    if (!dummy.toFile().exists() ){
      Files.createFile(dummy)
    }
  }

  @Test
  fun test1() {
    val syntax = SyntaxTree(QueryType.SELECT_QUERY)
    val recipe = SelectQueryRecipe()
    recipe.fromParts = arrayListOf("tb1")
    syntax.recipe = Optional.of(recipe)

    val session = ClientSession()
    val dbInfo = DatabaseInformation()

    val tb1 = TableJournal("tb1")
    tb1.fileName = "tb1.dummy"
    dbInfo.tables.add(tb1)
    session.dbPath = dbPath
    session.dbInfo = dbInfo
    engine.execute(syntax, session)
    println("OK")
  }
}