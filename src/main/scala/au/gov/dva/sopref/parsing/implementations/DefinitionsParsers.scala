package au.gov.dva.sopref.parsing.implementations

import scala.collection.mutable.ListBuffer

object DefinitionsParsers {

  def splitToDefinitions(definitionsSection : String) : List[String] = {
     assert(!definitionsSection.startsWith("\""))
    val acc = List[String]();
     val lines = definitionsSection.split("[\r\n]+").toList.drop(1)
     val result: List[String] = divideRecursive(acc,lines)
     return result

  }

  private def divideRecursive(divided : List[String], toDivide: List[String]) : List[String] = {

    if (toDivide.isEmpty)
      return (divided)
    else {
      assert(toDivide.head.startsWith("\""))
      val definitionLines = (toDivide.head :: toDivide.tail.takeWhile(s => !s.startsWith("\"")))
      val definition = definitionLines.mkString("\n")
      divideRecursive(definition :: divided,toDivide.drop(definitionLines.size))
    }
  }


}
