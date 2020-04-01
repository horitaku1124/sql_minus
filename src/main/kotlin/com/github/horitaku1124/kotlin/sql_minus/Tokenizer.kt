package com.github.horitaku1124.kotlin.sql_minus

import com.github.horitaku1124.kotlin.sql_minus.QueryType.CREATE_DATABASE
import java.lang.RuntimeException

class Tokenizer {
  fun parse(tokens: List<String>):List<SyntaxTree> {
    var syntaxTrees = arrayListOf<SyntaxTree>()

    var index = 0
    while (index < tokens.size) {
      var startToken = tokens[index++].toLowerCase()
      var syntax:SyntaxTree
      if (startToken == "create") {
        var objective = tokens[index++].toLowerCase()
        if (objective == "database") {
          syntax = SyntaxTree(CREATE_DATABASE)
        } else {
          throw RuntimeException("error")
        }
        var subject = tokens[index++]
        syntax.subject.add(subject)
      } else {
        throw RuntimeException("error")
      }
      while (index < tokens.size) {
        val token = tokens[index++]
        if (token == ";") {
          break
        }
      }
      syntaxTrees.add(syntax)
    }
    return syntaxTrees
  }
}