package au.gov.dva.sopref.parsing.implementations

import au.gov.dva.sopref.data.sops.StoredFactor
import au.gov.dva.sopref.interfaces.model.{Factor, SoP}
import au.gov.dva.sopref.parsing.traits.SoPParser

import scala.collection.immutable.{ListMap, Seq}
import scala.util.parsing.combinator.RegexParsers



class LsParser extends SoPParser with RegexParsers{


  // Parsers required:
  // head
  // tail
  // para
  // sub para
  // para terminator
  // factor text

  def paraParser : Parser[String] = """\([a-z]+\)""".r
  def bodyTextParser : Parser[String] = """[A-Za-z0-9\-'’,\)\(\s\.]+""".r
  def orTerminator : Parser[String] = """;\s+or""".r
  def periodTerminator : Parser[String] = """\.""".r
  def paraTerminatorParser : Parser[String] = orTerminator | periodTerminator
  def singleFactorParser: Parser[(String, String)] = paraParser ~ bodyTextParser <~ paraTerminatorParser ^^ {
    case para ~ factorText => (para,factorText)
  }

  def headParser : Parser[String] = """[A-Z]+[A-Za-z0-9\-'’,\)\(\s\.]+""".r <~ """:""".r


  def factorListParser : Parser[List[(String,String)]] = rep1(singleFactorParser) ^^  {
    case listOfFactors: Seq[(String, String)] => listOfFactors
      .sortBy(_._1)
  }

  def headAndFactorsParser : Parser[(String,List[(String,String)])] = headParser ~ factorListParser ^^ {
    case head ~ factorList => (head,factorList)
  }

  def parseFactorTextToParagraphs(factorsSectionText : String) : Map[String,String] = {
       null
  }






}
