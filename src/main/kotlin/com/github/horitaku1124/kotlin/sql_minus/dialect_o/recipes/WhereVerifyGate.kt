package com.github.horitaku1124.kotlin.sql_minus.dialect_o.recipes

import com.github.horitaku1124.kotlin.sql_minus.DBRuntimeException
import com.github.horitaku1124.kotlin.sql_minus.dialect_o.Record
import java.util.function.Predicate

class WhereVerifyGate(private var filter: Predicate<Record>) {

  fun isSatisfied(record: Record): Boolean {
    return filter.test(record)
  }

  companion object {
    fun andRule(subjectIndex: Int, operator: String, objective: String): WhereVerifyGate {
      val objectIsStr = objective.startsWith("'")
      val intValue: Int? = if (objectIsStr) null else objective.toInt()

      if (operator == "=") {
        return WhereVerifyGate(object: Predicate<Record>{
          override fun test(record: Record) : Boolean {
            val cell = record.cells[subjectIndex]
            if (objectIsStr) {

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
            if (objectIsStr) {

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
            if (objectIsStr) {

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
      } else {
        throw DBRuntimeException("error")
      }
    }
  }
}