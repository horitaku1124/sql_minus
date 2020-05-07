package com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes

import com.github.horitaku1124.kotlin.sql_minus.DBRuntimeException
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.Record
import java.util.function.Predicate

class WhereVerifyGate(private var filter: Predicate<Record>) {
  var andRules = arrayListOf<WhereVerifyGate>()
  var orRules = arrayListOf<WhereVerifyGate>()

  fun isSatisfied(record: Record): Boolean {
    if (!filter.test(record)) return false
    for (andRule in andRules) {
      if (!andRule.isSatisfied(record)) return false
    }
    return true
  }

  companion object {
    private val isIntReg = "^-?\\d+$".toRegex()
    fun addOperand(subjectIndex: Int, operator: String, objective: String): WhereVerifyGate {
      val objectIsInt = isIntReg.matches(objective)
      val intValue: Int? = if (objectIsInt) objective.toInt() else null

      if (operator == "=") {
        return WhereVerifyGate(object: Predicate<Record>{
          override fun test(record: Record) : Boolean {
            val cell = record.cells[subjectIndex]
            if (!objectIsInt) {

            } else {
//              println(" is => " + cell.intValue + " = " + intValue)
              if (cell.intValue == null) {
                return false
              } else if (cell.intValue == intValue) {
                return true
              }
            }
            return false
          }
        })
      } else if (operator == "<") {
        return WhereVerifyGate(object: Predicate<Record>{
          override fun test(record: Record) : Boolean {
            val cell = record.cells[subjectIndex]
            if (!objectIsInt) {

            } else {
//              println(" is => " + cell.intValue + " = " + intValue)
              if (cell.intValue == null) {
                return false
              } else if (cell.intValue!! < intValue!!) {
                return true
              }
            }
            return false
          }
        })
      } else if (operator == ">") {
        return WhereVerifyGate(object: Predicate<Record>{
          override fun test(record: Record) : Boolean {
            val cell = record.cells[subjectIndex]
            if (!objectIsInt) {

            } else {
//              println(" is => " + cell.intValue + " = " + intValue)
              if (cell.intValue == null) {
                return false
              } else if (cell.intValue!! > intValue!!) {
                return true
              }
            }
            return false
          }
        })
      } else if (operator == "is") {
        if (objective == "null") {
          return WhereVerifyGate(object: Predicate<Record>{
            override fun test(record: Record) : Boolean {
              val cell = record.cells[subjectIndex]
              return cell.isNull
            }
          })
        } else {
          throw DBRuntimeException("error")
        }
      } else {
        throw DBRuntimeException("error")
      }
    }
  }
}