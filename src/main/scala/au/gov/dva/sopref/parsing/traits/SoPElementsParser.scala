package au.gov.dva.sopref.parsing.traits

import java.time.LocalDate

import au.gov.dva.sopref.interfaces.model.InstrumentNumber

trait SoPElementsParser {
  def parseFactors(factorSection: String): Map[String, String]
  def parseDefinitions(definitionSection: String): Map[String, String]
  def parseCommencementDate(commencementSection: String): LocalDate
  def parseInstrumentNumber(plainTextSop : String) : InstrumentNumber
}
