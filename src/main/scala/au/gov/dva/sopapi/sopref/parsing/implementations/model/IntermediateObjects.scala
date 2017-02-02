package au.gov.dva.sopapi.sopref.parsing.implementations.model

import scala.util.Properties

// intermediate objects created by parsing SoP text
abstract class FactorInfo {
  def getLetter : String
  def getText : String
}

class FactorInfoWithoutSubParas(mainParaLetter : String, bodyText: String) extends FactorInfo
{
  override def getLetter: String = mainParaLetter

  override def getText: String = bodyText

  override def toString: String = mainParaLetter + " " + bodyText
}


class FactorInfoWithSubParas(mainParaLetter : String, head : String, subparas : List[(String,String)], tail :  Option[String]) extends FactorInfo
{
  private def format: String = head + "," + Properties.lineSeparator + subparas.map(sp => sp._1 + " " + sp._2).mkString("; or" + Properties.lineSeparator) + formatTail(tail)

  private def formatTail(tailOption : Option[String]) = if (tailOption.isDefined) "," + Properties.lineSeparator + tailOption.get
  else "";

  override def toString: String = format

  override def getLetter: String = mainParaLetter

  override def getText: String = this.format
}







