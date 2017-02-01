package au.gov.dva.sopapi.sopref.parsing.traits

import scala.util.parsing.combinator.RegexParsers


trait OsteoArthritisSubParasParser extends RegexParsers {

  // main para letter, list of sub paras with text, optional tail
  def mainParaLetterParser : Parser[String] = """\(([a-z])+\)""".r

  def subFactorBodyTextParser : Parser[String] = """[a-z\s]+""".r
  def headParser: Parser[String] = subFactorBodyTextParser <~ """,""".r

  def subParaLetterParser: Parser[String] = """\([ixv]+\)""".r

  def subParaBodyTextParser: Parser[String] = """[a-z0-9,\.\s]+""".r

  def subParaParser: Parser[(String, String)] = subParaLetterParser ~ subParaBodyTextParser ^^ {
    case letter ~ body => (letter, body)
  }

  def subParaTerminatorParser: Parser[String] = """; or""".r

  def subParaListParser: Parser[List[(String, String)]] = rep1sep(subParaParser, subParaTerminatorParser)

  def tailParser : Parser[String] = """; """.r ~> subFactorBodyTextParser

  def completeFactorWithSubParasParser : Parser[(String, String,List[(String,String)],Option[String])] = mainParaLetterParser ~ headParser ~ subParaListParser ~ opt(tailParser) ^^ {
    case mainParaLetter ~ head ~ paralist ~ tailOption => {
      (mainParaLetter, head,paralist,tailOption)
    }
  }






}
