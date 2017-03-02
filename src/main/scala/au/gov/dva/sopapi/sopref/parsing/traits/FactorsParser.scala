package au.gov.dva.sopapi.sopref.parsing.traits

import au.gov.dva.sopapi.dtos.StandardOfProof
import au.gov.dva.sopapi.exceptions.SopParserError
import au.gov.dva.sopapi.sopref.parsing.implementations.model.{FactorInfo, FactorInfoWithSubParas, FactorInfoWithoutSubParas, FactorInformation}

import scala.util.parsing.combinator.RegexParsers


trait FactorsParser extends RegexParsers with BodyTextParsers with TerminatorParsers {

  // main para letter, list of sub paras with text, optional tail
  def mainParaLetter: Parser[String] =
    """\(([a-z])+\)""".r

  private def factorsSectionHead : Parser[String] = mainFactorBodyText <~ ":"

  def head: Parser[String] = """[a-z\s]+""".r <~ """[,:]""".r

  def subParaLetter: Parser[String] = """\([ixv]+\)""".r

  def subPara: Parser[(String, String)] = subParaLetter ~ subParaBodyText ^^ {
    case letter ~ body => (letter, body)
  }

  def subParaList: Parser[List[(String, String)]] = rep1sep(subPara, orTerminator | andTerminator | semiColonTerminator)

  def tail: Parser[String] = not(orTerminator) ~> """; [a-z\s]+""".r

  def completeFactorWithSubParas: Parser[(String,String,List[(String,String)],Option[String])] = mainParaLetter ~ head ~ subParaList ~ opt(tail) ^^ {
    case mainParaLetter ~ head ~ paralist ~ tailOption => {
      (mainParaLetter,head,paralist,tailOption)
    }
  }

  def twoLevelPara : Parser[FactorInfoWithSubParas] = mainParaLetter ~ head ~ subParaList ~ opt(tail) ^^ {
    case  mainParaLetter ~ head ~ paralist ~ tailOption => new FactorInfoWithSubParas(mainParaLetter,head,paralist,tailOption)
  }

  def singleLevelPara : Parser[FactorInfoWithoutSubParas] = mainParaLetter ~ mainFactorBodyText ^^ {
    case para ~ text => new FactorInfoWithoutSubParas(para,text)
  }

  def factor : Parser[FactorInfo] = twoLevelPara | singleLevelPara ^^ {
    case factor => factor
  }

  def factorList : Parser[List[FactorInfo]] = rep1sep(factor, orTerminator) ^^ {
    case lf => lf
  }

  def factorsSection : Parser[(StandardOfProof,List[FactorInfo])] = factorsSectionHead ~ factorList <~ periodTerminator ^^ {
    case standardOfProof ~ factorList => {
      val standard = extractStandardOfProofFromHeader(standardOfProof)
      (standard,factorList)
    }
  }

  def parseFactorsSection(factorsSectionText : String) : (StandardOfProof,List[FactorInfo]) = {
    val result = this.parseAll(factorsSection,factorsSectionText)
    if (result.successful)
      return result.get;
    else {
      throw new SopParserError(s"Could not parse factors section: $result${scala.util.Properties.lineSeparator}$factorsSectionText.")
    }
  }

  def removeFactorTerminator(paragraph: String): String = {
    if (paragraph.endsWith("; or"))
      paragraph.dropRight(4)
    else if (paragraph.endsWith("."))
      paragraph.dropRight(1)
    else
      paragraph
  }

  def paragraph: Parser[FactorInformation] = mainParaLetter ~ factorText ^^ {
    case paragraph ~ text =>
      new FactorInformation(paragraph, removeFactorTerminator(text))
  }

  def parseFactorParagraph(factorText: String): FactorInformation = {
    val result = this.parseAll(paragraph, factorText)
    if (result.successful)
      result.get
    else
      throw new SopParserError(s"Could not parse factor paragraph: $result${scala.util.Properties.lineSeparator}$factorText")
  }

  def standardOfProof: Parser[StandardOfProof] = factorsSectionHead <~ factorText ^^ {
    case sectionHead =>
      val standard = extractStandardOfProofFromHeader(sectionHead)
      standard
  }

  def parseStandardOfProof(factorsSectionText: String): StandardOfProof = {
    val result = this.parseAll(standardOfProof, factorsSectionText)
    if (result.successful)
      result.get
    else {
      throw new SopParserError(s"Could not parse standard of proof: $result${scala.util.Properties.lineSeparator}$factorsSectionText")
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

trait BodyTextParsers extends RegexParsers {
  def mainFactorBodyText : Parser[String] = """(([A-Za-z0-9\-'’,\)\(\s]|\.(?=[A-Za-z0-9])))+""".r
  def subParaBodyText: Parser[String] = """([a-z0-9-,\s]|\.(?=[A-Za-z0-9]))+""".r
  def factorText : Parser[String] = """.+(?!\.$)""".r
}

trait TerminatorParsers extends RegexParsers {
  def orTerminator : Parser[String] = """; or""".r
  def andTerminator : Parser[String] = """; and""".r
  def semiColonTerminator : Parser[String] = """;""".r
  def periodTerminator: Parser[String] = """\.$""".r
}

