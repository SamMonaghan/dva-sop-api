package au.gov.dva.sopref.parsing.implementations

import au.gov.dva.sopref.data.sops.StoredFactor
import au.gov.dva.sopref.exceptions.SopParserError
import au.gov.dva.sopref.interfaces.model.{Factor, SoP, StandardOfProof}
import au.gov.dva.sopref.parsing.traits.SoPParser

import scala.collection.immutable.{ListMap, Seq}
import scala.util.parsing.combinator.RegexParsers



object LsParser extends SoPParser with RegexParsers{

  def paraParser : Parser[String] = """\([a-z]+\)""".r
  def bodyTextParser : Parser[String] = """(([A-Za-z0-9\-'’,\)\(\s]|(?<![a-zA-Z])\.))+""".r
  def orTerminator : Parser[String] = """;\s+or""".r
  def periodTerminator : Parser[String] = """\.$""".r
  def paraTerminatorParser : Parser[String] = orTerminator | periodTerminator
  def singleFactorParser: Parser[(String, String)] = paraParser ~ bodyTextParser <~ paraTerminatorParser ^^ {
    case para ~ factorText => (para,factorText)
  }

  def headParser : Parser[String] = bodyTextParser <~ """:""".r

  def paraAndTextParser : Parser[(String,String)] = paraParser ~ bodyTextParser ^^  {
    case para ~ text => (para,text)
  }

  def separatedFactorListParser : Parser[List[(String,String)]] = repsep(paraAndTextParser,orTerminator)  ^^ {
    case listOfFactors: Seq[(String, String)] => listOfFactors
  }

  def factorListParser : Parser[List[(String,String)]] = rep1(singleFactorParser) ^^  {
    case listOfFactors: Seq[(String, String)] => listOfFactors
      .sortBy(_._1)
  }

  def headAndFactorsParser : Parser[(String,List[(String,String)])] = headParser ~ factorListParser ^^ {
    case head ~ factorList => (head,factorList)
  }

  def completeFactorSectionParser : Parser[(String,List[(String,String)])] = headParser ~ separatedFactorListParser <~ periodTerminator  ^^ {
    case head ~ factorList => (head,factorList)
  }

  override def parseFactors(factorsSection: String): (StandardOfProof,List[(String, String)]) =
  {
    val result = this.parseAll(this.completeFactorSectionParser,factorsSection);
    if (!result.successful)
      throw new SopParserError(result.toString)
    else {
      val standardOfProof = extractStandardOfProofFromHeader(result.get._1)
      (standardOfProof,result.get._2)
    }
  }

  def extractStandardOfProofFromHeader(headerText : String) : StandardOfProof = {
    if (headerText.contains("balance of probabilities"))
      return StandardOfProof.BalanceOfProbabilities
    if (headerText.contains("reasonable hypothesis"))
      return StandardOfProof.ReasonableHypothesis
    else {
      throw new SopParserError("Cannot determine standard of proof from text: " + headerText)
    }
  }
}
