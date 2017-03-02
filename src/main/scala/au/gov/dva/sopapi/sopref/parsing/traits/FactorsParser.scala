package au.gov.dva.sopapi.sopref.parsing.traits

import au.gov.dva.sopapi.dtos.StandardOfProof
import au.gov.dva.sopapi.exceptions.SopParserError
import au.gov.dva.sopapi.sopref.parsing.SoPExtractorUtilities
import au.gov.dva.sopapi.sopref.parsing.implementations.model.{FactorInfo, FactorInfoWithSubParas, FactorInfoWithoutSubParas}

import scala.util.Properties
import scala.util.parsing.combinator.RegexParsers


trait FactorsParser extends RegexParsers with BodyTextParsers with TerminatorParsers {

  // main para letter, list of sub paras with text, optional tail
  def mainParaLetter: Parser[String] =
    """\(([a-z])+\)""".r

  private def factorsSectionHead : Parser[String] = mainFactorBodyText <~ ":"

  def head: Parser[String] = """[a-z\s]+""".r <~ """[,:]""".r

  def subParaLetter: Parser[String] = """\([ixv]+\)""".r

  def subPara: Parser[(String, String, Option[String])] = subParaLetter ~ subParaBodyText ~ opt(andTerminator | orTerminator) ^^ {
    case letter ~ body ~ terminator => (letter, body,terminator)
  }

  def subParaList: Parser[List[(String, String,Option[String])]] = rep1(subPara)

  def tail: Parser[String] = not(orTerminator) ~> """; [a-z\s]+""".r

  def completeFactorWithSubParas: Parser[(String,String,List[(String,String,Option[String])],Option[String])] = mainParaLetter ~ head ~ subParaList ~ opt(tail) ^^ {
    case mainParaLetter ~ head ~ subParalist ~ tailOption => {
      (mainParaLetter,head,subParalist,tailOption)
    }
  }

  def twoLevelPara : Parser[FactorInfoWithSubParas] = mainParaLetter ~ head ~ subParaList ~ opt(tail) <~ opt(orTerminator) ^^ {
    case  mainParaLetter ~ head ~ subParaList ~ tailOption => new FactorInfoWithSubParas(mainParaLetter,head,subParaList,tailOption)
  }

  def singleLevelPara : Parser[FactorInfoWithoutSubParas] = mainParaLetter ~ mainFactorBodyText <~ opt(orTerminator | andTerminator | semiColonTerminator) ^^ {
    case para ~ text => new FactorInfoWithoutSubParas(para,text)
  }

  def factor : Parser[FactorInfo] = twoLevelPara | singleLevelPara ^^ {
    case factor => factor
  }


  def parseFactorsSection(factorsSectionText : String) : (StandardOfProof,List[FactorInfo]) = {
    val splitToLines: List[String] = factorsSectionText.split("[\r\n]+").toList;
    val(header: String,rest: List[String]) = SoPExtractorUtilities.splitFactorsSectionToHeaderAndRest(splitToLines)

    val groupedToCollectionsOfFactors: List[String] =  SoPExtractorUtilities.splitFactorsSectionByFactor(rest)
      .map(factorLineCollection =>  factorLineCollection.mkString(" "))

    assert(groupedToCollectionsOfFactors.forall(i => !i.endsWith(" ") && !i.startsWith(" ")))

    val parsedFactors  = groupedToCollectionsOfFactors
      .map(this.parseAll(this.factor,_))

    val standard = extractStandardOfProofFromHeader(header)
    if (parsedFactors.forall(pf => pf.successful))
      return (standard, parsedFactors.map(pf => pf.get))
    else {
      val unsucessfulSections = parsedFactors.filter(!_.successful)
      throw new SopParserError(s"Could not parse factors section: ${unsucessfulSections.map(us => us.get).mkString(Properties.lineSeparator)}")
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
}

trait TerminatorParsers extends RegexParsers {
  def orTerminator : Parser[String] = """; or""".r
  def andTerminator : Parser[String] = """; and""".r
  def semiColonTerminator : Parser[String] = """;""".r
  def periodTerminator: Parser[String] = """\.$""".r
}

