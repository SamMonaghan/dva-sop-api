package au.gov.dva.sopapi.sopref.parsing.traits

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import au.gov.dva.sopapi.dtos.StandardOfProof
import au.gov.dva.sopapi.exceptions.SopParserError
import au.gov.dva.sopapi.interfaces.model.{DefinedTerm, InstrumentNumber}
import au.gov.dva.sopapi.sopref.parsing.implementations.model.{ParsedDefinedTerm, ParsedInstrumentNumber}
import au.gov.dva.sopapi.sopref.parsing.implementations.parsers.{DefinitionsParsers, Factor, FactorWithSubParas, FactorWithoutSubParas}

import scala.util.parsing.combinator.RegexParsers
import scala.collection.immutable.Seq

trait PreAugust2015SoPParser extends SoPParser with RegexParsers with FactorsParser {

  def paraLetterParser : Parser[String] = """\([a-z]+\)""".r
  def bodyTextParser : Parser[String] = """(([A-Za-z0-9\-'’,\)\(\s]|\.(?=[A-Za-z0-9])))+""".r

  def orTerminator : Parser[String] = """;\s+or""".r
  def periodTerminator : Parser[String] = """\.$""".r

  // singleParaParser is used in tests
  def andTerminator : Parser[String] = """;\s+and""".r
  def paraTerminatorParser : Parser[String] = orTerminator | andTerminator | periodTerminator
  def singleParaParser: Parser[(String, String)] = paraLetterParser ~ mainFactorBodyText <~ paraTerminatorParser ^^ {
    case para ~ factorText => (para, factorText)
  }

  def mainParaHeadParser : Parser[String] = mainFactorBodyText <~ ":"

  def paraAndTextParser : Parser[(String, String)] = paraLetterParser ~ mainFactorBodyText ^^ {
    case para ~ text => (para, text)
  }

  def separatedFactorListParser : Parser[List[(String, String)]] = repsep(paraAndTextParser, orTerminator) ^^ {
    case listOfFactors: Seq[(String, String)] => listOfFactors
  }


  def completeFactorSectionParser : Parser[(String,List[(String, String)])] = mainParaHeadParser ~ separatedFactorListParser <~ periodTerminator ^^ {
    case head ~ factorList => (head, factorList)
  }


  def extractStandardOfProofFromHeader(headerText: String): StandardOfProof = {
    if (headerText.contains("balance of probabilities"))
      return StandardOfProof.BalanceOfProbabilities
    if (headerText.contains("reasonable hypothesis"))
      return StandardOfProof.ReasonableHypothesis
    else {
      throw new SopParserError("Cannot determine standard of proof from text: " + headerText)
    }
  }



  override def parseFactors(factorsSection: String): (StandardOfProof, List[(String, String)]) =
  {
    val result = this.parseAll(this.completeFactorSectionParser, factorsSection)
    if (!result.successful)
      throw new SopParserError(result.toString)
    else {
      val standardOfProof = extractStandardOfProofFromHeader(result.get._1)
      (standardOfProof, result.get._2)
    }
  }

  override def parseInstrumentNumber(citationSection: String): InstrumentNumber = {
    val instrumentNumberRegex = """No\.?\s+([0-9]+)\s+of\s+([0-9]{4,4})""".r
    val regexMatch = instrumentNumberRegex.findFirstMatchIn(citationSection)
    if (regexMatch.isEmpty)
      throw new SopParserError("Cannot determine instrument number from this citation: " + citationSection)

    val number = regexMatch.get.group(1).toInt
    val year = regexMatch.get.group(2).toInt
    new ParsedInstrumentNumber(number.toInt, year.toInt)
  }

  override def parseDefinitions(definitionsSection: String): List[DefinedTerm] = {
    DefinitionsParsers.splitToDefinitions(definitionsSection)
      .map(DefinitionsParsers.parseSingleDefinition(_))
      .map(t => new ParsedDefinedTerm(t._1, t._2))
  }

  override def parseDateOfEffect(dateOfEffectSection: String): LocalDate = {
    val doeRegex = """effect from ([0-9]+\s+[A-Za-z]+\s+[0-9]{4,4})""".r
    val m = doeRegex.findFirstMatchIn(dateOfEffectSection)
    if (m.isEmpty)
      throw new SopParserError("Cannot determine date of effect from: " + dateOfEffectSection)
    return LocalDate.parse(m.get.group(1), DateTimeFormatter.ofPattern("d MMMM yyyy"))
  }

  // Used for single aggravation paragraph (eg. Paragraph 6(b) applies)
  override def parseAggravationPara(aggravationSection: String): (String, String) = {
    val paraIntervalRegex = """Paragraph [0-9]+(\([a-z]+\))""".r
    val m = paraIntervalRegex.findFirstMatchIn(aggravationSection)
    if (m.isEmpty)
      throw new SopParserError("Cannot determine aggravation para from: " + aggravationSection)
    (m.get.group(1), m.get.group(1))
  }

  // Used for aggravation interval (eg. Paragraphs 6(u) to 6(nn) apply)
  override def parseStartAndEndAggravationParas(aggravationSection: String): (String, String) = {
    val paraIntervalRegex = """Paragraphs [0-9]+(\([a-z]+\)) to [0-9]*(\([a-z]+\))""".r
    val m = paraIntervalRegex.findFirstMatchIn(aggravationSection)
    if (m.isEmpty)
      throw new SopParserError("Cannot determine aggravation paras from: " + aggravationSection)
    (m.get.group(1), m.get.group(2))
  }

  override def parseCitation(citationSection: String): String = {
    val regex = """may be cited as (.*)""".r
    val m = regex.findFirstMatchIn(citationSection)
    if (m.isEmpty)
      throw new SopParserError("Cannot get citation from: " + citationSection)
    val trimmed = m.get.group(1).stripSuffix(".")
    trimmed
  }

  override def parseConditionNameFromCitation(citation: String): String = {
    val regex = """Statement of Principles concerning (([A-Za-z-'\s](?!No\.))*)""".r

    val m = regex.findFirstMatchIn(citation)
    if (m.isEmpty)
      throw new SopParserError("Cannot get condition name from this citation: %s".format(citation))
    return m.get.group(1)
  }

}
















