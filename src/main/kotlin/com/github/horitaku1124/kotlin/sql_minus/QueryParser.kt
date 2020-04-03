package com.github.horitaku1124.kotlin.sql_minus

class QueryParser {
  private val symbols = listOf('+', '-', '/', '*', '=','<', '>')
  fun lexicalAnalysis(query: String): List<String> {
    val verseList = arrayListOf<String>()
    var verse = ""
    var quote: Char? = null
    var inOneLineComment = false

    val sql = query.toCharArray()
    var i = -1
    while (i < sql.size - 1) {
      i++
      val char = sql[i]
      if (inOneLineComment) {
        if (char == '\n') {
          if (verse != "") {
            verseList.add(verse)
          }
          verse = ""
          inOneLineComment = false
        } else {
          continue
        }
      }
      if(quote !== null) {
        if(char == quote) {
          verse = quote + verse + quote
          verseList.add(verse)
          verse = ""
          quote = null
        } else {
          verse += char
        }
        continue
      }
      if (char == '-') {
        val next = sql[i + 1]
        if (next == '-') {
          if (verse != "") {
            verseList.add(verse)
          }
          verse = "--"
          inOneLineComment = true
          i += 1
          continue
        }
      }

      if(char == '\'' || char == '"' || char == '`') {
        quote = char
      } else if(symbols.contains(char)) {
        if(verse != "") {
          verseList.add(verse)
          verse = ""
        }

        verseList.add(char.toString())
      } else if(char == ' ' || char == '\t' || char == '\r' || char == '\n') {
        if(verse !== "") {
          verseList.add(verse)
        }
        verse = ""
      } else if(char == ',' || char == '(' || char == ')') {
        if(verse !== "") {
          verseList.add(verse)
        }
        verseList.add(char.toString())
        verse = ""
      } else if(char == ';') {
        if(verse != "") {
          verseList.add(verse)
          verse = ""
        }
        verseList.add(char.toString())
      } else {
        verse += char
      }
    }
    if(verse != "") {
      verseList.add(verse)
    }
    return verseList
  }
}