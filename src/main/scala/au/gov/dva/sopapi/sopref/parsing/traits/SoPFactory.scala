package au.gov.dva.sopapi.sopref.parsing.traits

import java.time.LocalDate

import au.gov.dva.sopapi.dtos.StandardOfProof
import au.gov.dva.sopapi.interfaces.model.{DefinedTerm, Factor, ICDCode, SoP}
import au.gov.dva.sopapi.sopref.parsing.SoPExtractorUtilities
import au.gov.dva.sopapi.sopref.parsing.implementations.model.{FactorInfo, ParsedFactor, ParsedSop}
import au.gov.dva.sopapi.sopref.parsing.implementations.parsers.PreAugust2015Parser


trait SoPFactory {
  def create(registerId: String, cleansedText: String): SoP

  def create(registerId : String, cleansedText: String, extractor : SoPExtractor, parser: SoPParser) = {
    val citation = parser.parseCitation(extractor.extractCitation(cleansedText));
    val instrumentNumber = parser.parseInstrumentNumber(citation);

    val definedTermsList: List[DefinedTerm] = parser.parseDefinitions(extractor.extractDefinitionsSection(cleansedText))

    val factorsSection: (Int, List[String]) = extractor.extractFactorsSection(cleansedText)

    val splitFactors: List[String] = SoPExtractorUtilities.splitFactorsSectionByFactor(factorsSection._2).map(f => combineFactorLines(f))
    val factorInfos: List[FactorInfo] = splitFactors.map(f => parser.parseFactorParagraph(f))

    val factorObjects: List[Factor] = this.buildFactorObjectsFromInfo(factorInfos, factorsSection._1, definedTermsList)

    val startAndEndOfAggravationParas = parser.parseStartAndEndAggravationParas(extractor.extractAggravationSection(cleansedText))

    val(onsetFactors,aggravationFactors) = divideFactorObjectsToOnsetAndAggravation(factorObjects,startAndEndOfAggravationParas._1,startAndEndOfAggravationParas._2)

    val effectiveFromDate: LocalDate = parser.parseDateOfEffect(extractor.extractDateOfEffectSection(cleansedText))

    val standardOfProof = parser.parseStandardOfProof(factorsSection._2.mkString(" "))

    val icdCodes: List[ICDCode] = extractor.extractICDCodes(cleansedText)

    val conditionName = PreAugust2015Parser.parseConditionNameFromCitation(citation);

    new ParsedSop(registerId,instrumentNumber,citation,aggravationFactors, onsetFactors, effectiveFromDate,standardOfProof,icdCodes,conditionName)
  }

  def combineFactorLines(factorLines: List[String]): String = {
    factorLines.foldLeft("") {
      (line: String, nextLine: String) =>
        if (line.endsWith("-"))
          // Concatenate hyphenated lines without a space
          line.splitAt(line.lastIndexOf("-"))._1.concat(nextLine)
        else
          line.concat(" ").concat(nextLine)
    }
  }

  def stripParaNumber(paraWithNumber : String): String = {
    assert(!paraWithNumber.takeWhile(c => c.isDigit).isEmpty)
    paraWithNumber.dropWhile(c => c.isDigit)
  }

  def divideFactorObjectsToOnsetAndAggravation(factorObjects: List[Factor], startParaLetterOfAgg : String, endParaLetterOfAgg : String) = {
    val orderedParaLetters = factorObjects.map(f => f.getParagraph.dropWhile(c => c.isDigit)).toList

    val splitOfOnsetAndAggravationFactors =  this.splitFactors(
      orderedParaLetters,startParaLetterOfAgg,endParaLetterOfAgg)

    val onsetParasWithoutNumber = splitOfOnsetAndAggravationFactors._1;
    val aggParasWithoutNumber = splitOfOnsetAndAggravationFactors._2;

    val onsetFactors = factorObjects
      .filter(f => onsetParasWithoutNumber.contains(stripParaNumber(f.getParagraph)))
    val aggravationFactors = factorObjects
      .filter(f => aggParasWithoutNumber.contains(stripParaNumber(f.getParagraph)))

    (onsetFactors,aggravationFactors)

  }

  def splitFactors(parasInOrder: List[String], startPara: String, endPara: String) = {
    val firstChunkOfOnsetParas = parasInOrder.takeWhile(i => i != startPara);
    val lastChunkOfOnsetParas = parasInOrder.reverse.takeWhile(i => i != endPara).reverse;
    val allOnsetParas = firstChunkOfOnsetParas ++ lastChunkOfOnsetParas

    val aggParas = parasInOrder.filter(p => !allOnsetParas.contains(p))
    (allOnsetParas, aggParas)
  }

  def buildFactorObjectsFromInfo(factors: List[FactorInfo], factorSectionNumber: Int, definedTerms: List[DefinedTerm]): List[Factor] = {
    factors
      .map(fi => (factorSectionNumber.toString.concat(fi.getLetter), fi.getText))
      .map(i => {

        val relevantDefinitions = definedTerms.filter(d => i._2.contains(d.getTerm)).toSet
        new ParsedFactor(i._1, i._2, relevantDefinitions)
      })


  }

  def buildFactorObjects(factorData: List[(String, String)], factorSectionNumber: Int, definedTerms: List[DefinedTerm]): List[Factor] = {

    factorData
      .map(f => (factorSectionNumber.toString.concat(f._1), f._2)) // prepend para number to letter
      .map(i => {
      val relevantDefinitions = definedTerms.filter(d => i._2.contains(d.getTerm)).toSet
      new ParsedFactor(i._1, i._2, relevantDefinitions)
    }
    )
  }
}
