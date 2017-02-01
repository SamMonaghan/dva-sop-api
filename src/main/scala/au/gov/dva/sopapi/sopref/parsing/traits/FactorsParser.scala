package au.gov.dva.sopapi.sopref.parsing.traits

import au.gov.dva.sopapi.dtos.StandardOfProof
import au.gov.dva.sopapi.exceptions.SopParserError
import au.gov.dva.sopapi.sopref.parsing.implementations.parsers.{Factor, FactorWithSubParas, FactorWithoutSubParas}

import scala.collection.immutable.Seq
import scala.util.parsing.combinator.RegexParsers


trait FactorsParser extends RegexParsers {

  // main para letter, list of sub paras with text, optional tail
  def mainParaLetter: Parser[String] =
    """\(([a-z])+\)""".r

  def mainFactorBodyText : Parser[String] = """(([A-Za-z0-9\-'’,\)\(\s]|\.(?=[A-Za-z0-9])))+""".r

  private def factorsSectionHead : Parser[String] = mainFactorBodyText <~ ":"

  def head: Parser[String] = """[a-z\s]+""".r <~ """,""".r

  private def orTerminator = """; or""".r
  private def periodTerminator = """\.$""".r

  def subParaLetter: Parser[String] = """\([ixv]+\)""".r

  def subParaBodyText: Parser[String] = """([a-z0-9-,\s]|\.(?=[A-Za-z0-9]))+""".r

  def subPara: Parser[(String, String)] = subParaLetter ~ subParaBodyText ^^ {
    case letter ~ body => (letter, body)
  }

  def subParaTerminator: Parser[String] = """; or""".r

  def subParaList: Parser[List[(String, String)]] = rep1sep(subPara, subParaTerminator)

  def tail: Parser[String] = not(orTerminator) ~> """; [a-z\s]+""".r

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

  def factor : Parser[Factor] = twoLevelPara | singleLevelPara ^^ {
    case factor => factor
  }


  def factorList : Parser[List[Factor]] = rep1sep(factor, orTerminator) ^^ {
    case lf => lf
  }

    def parseFactorSection : Parser[(StandardOfProof,List[Factor])] = factorsSectionHead ~ factorList <~ periodTerminator ^^ {
    case standardOfProof ~ factorList => {
      val standard = extractStandardOfProofFromHeader(standardOfProof)
      (standard,factorList)
    }
  }

  private def extractStandardOfProofFromHeader(headerText: String): StandardOfProof = {
    if (headerText.contains("balance of probabilities"))
      return StandardOfProof.BalanceOfProbabilities
    if (headerText.contains("reasonable hypothesis"))
      return StandardOfProof.ReasonableHypothesis
    else {
      throw new SopParserError("Cannot determine standard of proof from text: " + headerText)
    }
  }

}
