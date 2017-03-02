package au.gov.dva.sopapi.sopref.parsing.implementations.model

import scala.util.Properties

// intermediate objects created by parsing SoP text
abstract class FactorInfo {
  def getLetter : String
  def getText : String
}

class FactorInformation(paragraphLetter: String, factorText: String) extends FactorInfo
{
  override def getLetter: String = paragraphLetter.toLowerCase()

  override def getText: String = factorText

  override def toString: String = getLetter + " " + factorText
}

class FactorInfoWithoutSubParas(mainParaLetter : String, bodyText: String) extends FactorInfo
{
  override def getLetter: String = mainParaLetter.toLowerCase()

  override def getText: String = bodyText

  override def toString: String = getLetter + " " + bodyText
}


class FactorInfoWithSubParas(mainParaLetter : String, head : String, subparas : List[(String,String)], tail :  Option[String]) extends FactorInfo
{
  private def format: String = getLetter + " " +  head + "," + Properties.lineSeparator + subparas.map(sp => sp._1 + " " + sp._2).mkString("; or" + Properties.lineSeparator) + formatTail(tail)

  private def formatTail(tailOption : Option[String]) = if (tailOption.isDefined) "," + Properties.lineSeparator + tailOption.get
  else "";

  override def toString: String = format

  override def getLetter: String = mainParaLetter.toLowerCase()

  override def getText: String = this.format
}







