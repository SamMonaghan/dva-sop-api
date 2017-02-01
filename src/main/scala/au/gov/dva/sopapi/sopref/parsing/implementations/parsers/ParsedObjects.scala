package au.gov.dva.sopapi.sopref.parsing.implementations.parsers

import au.gov.dva.sopapi.interfaces.model.DefinedTerm
import com.google.common.collect.ImmutableSet

import scala.util.Properties

abstract class Factor {
  def getLetter : String
  def getText : String
}

class FactorWithoutSubParas(mainParaLetter : String, bodyText: String) extends Factor
{
  override def getLetter: String = mainParaLetter

  override def getText: String = bodyText

  override def toString: String = mainParaLetter + " " + bodyText
}


class FactorWithSubParas(mainParaLetter : String, head : String, subparas : List[(String,String)], tail :  Option[String]) extends Factor
{
  private def format: String = head + "," + Properties.lineSeparator + subparas.map(sp => sp._1 + " " + sp._2).mkString("; or" + Properties.lineSeparator) + formatTail(tail)

  private def formatTail(tailOption : Option[String]) = if (tailOption.isDefined) "," + Properties.lineSeparator + tailOption.get
  else "";

  override def toString: String = format

  override def getLetter: String = mainParaLetter

  override def getText: String = this.format
}







