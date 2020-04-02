package com.github.horitaku1124.kotlin.sql_minus

import com.github.horitaku1124.kotlin.sql_minus.QueryType.CHANGE_DATABASE
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
          throw RuntimeException("unrecognized command => $objective")
        }
        var subject = tokens[index++]
        syntax.subject.add(subject)
      } else if (startToken == "connect") {
        syntax = SyntaxTree(CHANGE_DATABASE)
        var subject = tokens[index++].toLowerCase()
        syntax.subject.add(subject)
      } else  {
        throw RuntimeException("unrecognized command => $startToken")
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