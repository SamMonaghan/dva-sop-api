package au.gov.dva.sopapi.sopref.parsing.traits

import au.gov.dva.sopapi.sopref.parsing.implementations.parsers.{Factor, FactorWithSubParas, FactorWithoutSubParas}

import scala.collection.immutable.Seq
import scala.util.parsing.combinator.RegexParsers


trait FactorsParser extends RegexParsers {

  // main para letter, list of sub paras with text, optional tail
  def mainParaLetter: Parser[String] =
    """\(([a-z])+\)""".r

  def subFactorBodyText: Parser[String] = """[a-z\s]+""".r

  def mainFactorBodyText : Parser[String] = """(([A-Za-z0-9\-'’,\)\(\s]|\.(?=[A-Za-z0-9])))+""".r
  def head: Parser[String] = subFactorBodyText <~ """,""".r

  private def orTerminator = """; or""".r

  def subParaLetter: Parser[String] = """\([ixv]+\)""".r

  def subParaBodyText: Parser[String] = """[a-z0-9,\.\s]+""".r

  def subPara: Parser[(String, String)] = subParaLetter ~ subParaBodyText ^^ {
    case letter ~ body => (letter, body)
  }

  def subParaTerminator: Parser[String] = """; or""".r

  def subParaList: Parser[List[(String, String)]] = rep1sep(subPara, subParaTerminator)

  def tail: Parser[String] = """; """.r ~> subFactorBodyText


  def completeFactorWithSubParas: Parser[(String,String,List[(String,String)],Option[String])] = mainParaLetter ~ head ~ subParaList ~ opt(tail) ^^ {
    case mainParaLetter ~ head ~ paralist ~ tailOption => {
      (mainParaLetter,head,paralist,tailOption)
    }
  }

  def twoLevelPara : Parser[FactorWithSubParas] = mainParaLetter ~ head ~ subParaList ~ opt(tail) ^^ {
    case  mainParaLetter ~ head ~ paralist ~ tailOption => new FactorWithSubParas(mainParaLetter,head,paralist,tailOption)
  }

  def singleLevelPara : Parser[FactorWithoutSubParas] = mainParaLetter ~ mainFactorBodyText ^^ {
    case para ~ text => new FactorWithoutSubParas(para,text)
  }

  def parseFactor : Parser[Factor] = twoLevelPara | singleLevelPara ^^ {
    case factor => factor
  }


  def parseFactorList : Parser[List[Factor]] = repsep(parseFactor, orTerminator) ^^ {
    case lf => lf
  }
}
