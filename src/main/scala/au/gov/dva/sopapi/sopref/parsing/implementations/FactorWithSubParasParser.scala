package au.gov.dva.sopapi.sopref.parsing.implementations

import java.io.Serializable

import au.gov.dva.sopapi.sopref.parsing.traits.PreAugust2015SoPParser

import scala.util.parsing.combinator.RegexParsers


trait OsteoArthritisSubParasParser extends RegexParsers {

  // main para letter, list of sub paras with text, optional tail
  def parseFactorParaWithSubParas: Parser[(String, List[(String, String)], Option[String])]


  def headParser: Parser[String] = """for [a-z]+,""".r

  def subParaLetterParser: Parser[String] = """\([ixv]+\)""".r

  def subParaBodyTextParser: Parser[String] = """[a-z0-9,\.\s]+""".r

  def subParaParser: Parser[(String, String)] = subParaLetterParser ~ subParaBodyTextParser ^^ {
    case letter ~ body => (letter, body)
  }

  def subParaTerminatorParser: Parser[String] = """; or""".r

  def subParaListParser: Parser[List[(String, String)]] = rep1sep(subParaParser, subParaTerminatorParser)

  def tailTextParser : Parser[String] = """for [a-z]+""".r

  def tailParser : Parser[String] = """;""".r ~> """for [a-z]+""".r

  def subParaTailParser : Parser[String] =  tailParser | subParaTerminatorParser ^^
  {
    case s  => if (s.startsWith("for")) s else ""
  }

  def completeFactorWithSubParasParser : Parser[(String,List[(String,String)],Option[String])] = headParser ~ subParaListParser ~ subParaTailParser ^^ {
    case head ~ paralist ~ tail => {
      val tailOption = if (tail.isEmpty) None else Some(tail)
      (head,paralist,tailOption)
    }
  }




}
