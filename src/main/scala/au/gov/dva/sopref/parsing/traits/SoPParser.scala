package au.gov.dva.sopref.parsing.traits

import au.gov.dva.sopref.interfaces.model.{Factor, InstrumentNumber, SoP, StandardOfProof}

trait SoPParser {
  def parseFactors(factorsSection : String) : (StandardOfProof, List[(String,String)])
  def parseInstrumentNumber(citationSection : String) : InstrumentNumber
}
















