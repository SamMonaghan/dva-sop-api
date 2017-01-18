package au.gov.dva.sopapi.sopref.parsing.implementations

import java.time.LocalDate

import au.gov.dva.sopapi.interfaces.model.{DefinedTerm, ICDCode, SoP}
import au.gov.dva.sopapi.dtos.StandardOfProof
import au.gov.dva.sopapi.sopref.parsing.traits.SoPFactory

object LsSoPFactory extends SoPFactory{
  override def create(registerId : String, rawText : String, cleansedText: String): SoP = {
    val extractor = new PreAugust2015Extractor();
    val citation = LsParser.parseCitation(extractor.extractCitation(cleansedText));
    val instrumentNumber = LsParser.parseInstrumentNumber(citation);

    val definedTermsList: List[DefinedTerm] = LsParser.parseDefinitions(extractor.extractDefinitionsSection(cleansedText))

    val factorsSection: (Int, String) = extractor.extractFactorSection(cleansedText)
    val factors: (StandardOfProof, List[(String, String)]) = LsParser.parseFactors(factorsSection._2)

    val factorObjects = this.buildFactorObjects(factors._2,factorsSection._1,definedTermsList)

    val startAndEndOfAggravationParas = LsParser.parseStartAndEndAggravationParas(extractor.extractAggravationSection(cleansedText))
    val splitOfOnsetAndAggravationFactors = this.splitFactors(factors._2.map(_._1),startAndEndOfAggravationParas._1,startAndEndOfAggravationParas._2)

    val onsetFactors = buildFactorObjects(
      factors._2.filter(f =>  splitOfOnsetAndAggravationFactors._1.contains(f._1)),
      factorsSection._1,
      definedTermsList)

    val aggravationFactors = buildFactorObjects(
      factors._2.filter(f =>  splitOfOnsetAndAggravationFactors._2.contains(f._1)),
      factorsSection._1,
      definedTermsList)

    val effectiveFromDate: LocalDate = LsParser.parseDateOfEffect(extractor.extractDateOfEffectSection(cleansedText))

    val standardOfProof = factors._1

    val icdCodes: List[ICDCode] = extractor.extractICDCodes(cleansedText)

    val conditionName = LsParser.parseConditionNameFromCitation(citation);

    new ParsedSop(registerId,instrumentNumber,citation,aggravationFactors, onsetFactors, effectiveFromDate,standardOfProof,icdCodes,conditionName)
  }



}
