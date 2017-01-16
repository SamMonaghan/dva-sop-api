package au.gov.dva.sopapi.sopref.parsing.implementations

import au.gov.dva.sopapi.interfaces.model.ICDCode
import au.gov.dva.sopapi.sopref.parsing.SoPExtractorUtilities
import au.gov.dva.sopapi.sopref.parsing.traits.SoPExtractor

class CartilageTearExtractor extends SoPExtractor {

  override def extractFactorSection(plainTextSop: String): (Int,String) = {
    val headingRegex = """^Factors$""".r;
    val factorsSection = SoPExtractorUtilities.getSection(plainTextSop,headingRegex)
    (factorsSection._1,factorsSection._2.mkString(" "))
  }

  override def extractDefinitionsSection(plainTextSop: String): String = {
    val headingRegex = """^Other definitions$""".r
    val definitionsSection = SoPExtractorUtilities.getSection(plainTextSop,headingRegex);
    definitionsSection._2.mkString("\n")
  }

  override def extractDateOfEffectSection(plainTextSop: String): String =  {
    val doeRegex = """Date of effect""".r
    val doeToEnd = SoPExtractorUtilities.getSection(plainTextSop, doeRegex);
    doeToEnd._2.take(1).mkString
  }

  override def extractCitation(plainTextSop: String): String = {
    val citationRegex = "^Title$".r
    SoPExtractorUtilities.getSection(plainTextSop,citationRegex)._2.mkString(" ")
  }

  // No ICD codes in F2010L01666 and F2010L01667
  override def extractICDCodes(plainTextSop: String): List[ICDCode] = {
    val individualsCodes = Nil
    individualsCodes.toList
  }

  override def extractAggravationSection(plainTextSop: String): String = {
    val aggravationSectionRegex = """Factors that apply only to material contribution or aggravation""".r
    SoPExtractorUtilities.getSection(plainTextSop,aggravationSectionRegex)._2.mkString(" ")
  }
}
