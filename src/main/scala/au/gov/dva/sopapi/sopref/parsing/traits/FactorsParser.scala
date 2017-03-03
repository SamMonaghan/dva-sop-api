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

  private def factorsSectionHead: Parser[String] = mainFactorBodyText <~ ":"

  def head: Parser[String] = """[a-z\s]+""".r <~ """[,:]""".r

  def subParaLetter: Parser[String] = """\([ixv]+\)""".r

  def subPara: Parser[(String, String, Option[String])] = subParaLetter ~ subParaBodyText ~ opt(andTerminator | orTerminator | semiColonTerminator) ^^ {
    case letter ~ body ~ terminator => (letter, body, terminator)
  }

  def subParaList: Parser[List[(String, String, Option[String])]] = rep1(subPara)

  def tail: Parser[String] = not(orTerminator) ~> (";" | ",") ~> Properties.lineSeparator ~> """[a-z,\s]+""".r <~ (periodTerminator | orTerminator)

  def completeFactorWithSubParas: Parser[(String, String, List[(String, String, Option[String])], Option[String])] = mainParaLetter ~ head ~ subParaList ~ opt(tail) ^^ {
    case mainParaLetter ~ head ~ subParalist ~ tailOption => {
      (mainParaLetter, head, subParalist, tailOption)
    }
  }

  def twoLevelPara: Parser[FactorInfoWithSubParas] = mainParaLetter ~ head ~ subParaList ~ opt(tail) ^^ {
    case mainParaLetter ~ head ~ subParaList ~ tailOption => new FactorInfoWithSubParas(mainParaLetter, head, subParaList, tailOption)
  }

  def singleLevelPara: Parser[FactorInfoWithoutSubParas] = mainParaLetter ~ mainFactorBodyText <~ opt(orTerminator | periodTerminator) ^^ {
    case para ~ text => new FactorInfoWithoutSubParas(para, text)
  }

  def subParaWithOptTail: Parser[(String, String, Option[String])] = subParaLetter ~ subParaBodyText ~ opt(orTerminator | periodTerminator | tail) ^^ {
    case letter ~ body ~ tail => (letter, body, tail)
  }

  def factor: Parser[FactorInfo] = (twoLevelPara | singleLevelPara) <~ opt(orTerminator | periodTerminator) ^^ {
    case factor => factor
  }

  def factorHead: Parser[(String, String)] = mainParaLetter ~ mainFactorBodyText <~ opt(":" | ",") ^^ {
    case letter ~ body => (letter, body)
  }

  def splitOutTailIfAny(lastSubParaWithLineBreaks: String): (String, Option[String]) = {
    val asLines = lastSubParaWithLineBreaks.split("[\r\n]+").toList
    val reversed = asLines.reverse
    val tail = reversed.takeWhile(l => !l.endsWith(";")).reverse
    if (tail.size < asLines.size) {
      return (asLines.take(asLines.size - tail.size).mkString(Properties.lineSeparator), Some(tail.mkString(Properties.lineSeparator)))
    }
    else return (lastSubParaWithLineBreaks, None)
  }

  def parseSingleFactor(singleFactorTextInclLineBreaks: String): FactorInfo = {
    // split to head and rest
    // split the rest to sub paras, with any tail stuck on the last para
    // separate out the tail
    val (head, rest) = SoPExtractorUtilities.splitFactorToHeaderAndRest(singleFactorTextInclLineBreaks.split("[\r\n]+").toList)
    assert(!head.isEmpty)

    val headParseResult = this.parseAll(this.factorHead, head)
    if (!headParseResult.successful) throw new SopParserError(headParseResult.toString)

    if (rest.isEmpty) {
      val simpleFactorParseResult = this.parseAll(this.singleLevelPara, head)
      if (!simpleFactorParseResult.successful) throw new SopParserError(simpleFactorParseResult.toString)
      else return simpleFactorParseResult.get
    }

    val restSplitToSubParas = SoPExtractorUtilities.splitFactorToSubFactors(rest)
      .map(i => i.mkString(Properties.lineSeparator))

    val subFactorTextsExceptLast = restSplitToSubParas.dropRight(1)
    val parseSubFactorsExceptLast = subFactorTextsExceptLast
      .map(this.parseAll(this.subPara, _))

    if (parseSubFactorsExceptLast.exists(p => !p.successful)) {
      val unsucessful = parseSubFactorsExceptLast.filter(!_.successful)
      val msg = unsucessful.map(us => us.toString).mkString(Properties.lineSeparator)
      throw new SopParserError(msg)
    }
    else {
      val last = restSplitToSubParas.takeRight(1).map(this.parseAll(this.subParaWithOptTail, _)).headOption
      if (last.isEmpty) throw new SopParserError("Last sub para is not defined: " + Properties.lineSeparator + singleFactorTextInclLineBreaks)
      val lastParaParseResult = last.get
      if (!lastParaParseResult.successful) throw new SopParserError("Could not parse last sub para: " + lastParaParseResult.toString)

      val headLetter = headParseResult.get._1
      val headText = headParseResult.get._2
      val allSubParaInfosButLast: List[(String, String, Option[String])] = parseSubFactorsExceptLast.map(r => r.get)
      // option in last sub para is tail, not terminator
      val lastSubParaInfoResult = last.get.get
      val lastSubParaInfo: (String, String, Option[String]) = (lastSubParaInfoResult._1, lastSubParaInfoResult._2, None)
      val allSubParas = allSubParaInfosButLast :+ lastSubParaInfo
      val tail = lastSubParaInfo._3
      return new FactorInfoWithSubParas(headLetter, headText, allSubParas, tail)

    }

  }


  def parseFactorsSection(factorsSectionText: String): (StandardOfProof, List[FactorInfo]) = {
    val splitToLines: List[String] = factorsSectionText.split("[\r\n]+").toList;
    val (header: String, rest: List[String]) = SoPExtractorUtilities.splitFactorsSectionToHeaderAndRest(splitToLines)

    val groupedToCollectionsOfFactors: List[String] = SoPExtractorUtilities.splitFactorsSectionByFactor(rest)
      .map(factorLineCollection => factorLineCollection.mkString(Properties.lineSeparator))

    assert(groupedToCollectionsOfFactors.forall(i => !i.endsWith(" ") && !i.startsWith(" ")))

    val parsedFactors = groupedToCollectionsOfFactors
      .map(this.parseAll(this.factor, _))

    val standard = extractStandardOfProofFromHeader(header)
    if (parsedFactors.forall(pf => pf.successful))
      return (standard, parsedFactors.map(pf => pf.get))
    else {
      val unsucessfulSections = parsedFactors.filter(!_.successful)
      val msg = unsucessfulSections.map(us => us.toString).mkString(Properties.lineSeparator)
      throw new SopParserError(msg)
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

  def mainFactorBodyText: Parser[String] = """(([A-Za-z0-9\-'’,\)\(\s]|\.(?=[A-Za-z0-9])))+""".r

  def subParaBodyText: Parser[String] = """([a-z0-9-,\s]|\.(?=[A-Za-z0-9]))+""".r
}

trait TerminatorParsers extends RegexParsers {
  def orTerminator: Parser[String] = """; or""".r

  def andTerminator: Parser[String] = """; and""".r

  def semiColonTerminator: Parser[String] = """;""".r

  def periodTerminator: Parser[String] = """\.$""".r
}

