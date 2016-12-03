package au.gov.dva.sopref.parsing.traits

import au.gov.dva.sopref.interfaces.model.{Factor, SoP, StandardOfProof}

trait SoPParser {
  def parseFactors(factorsSection : String) : (StandardOfProof, List[(String,String)])

}
















