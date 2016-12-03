package au.gov.dva.sopref.parsing.traits

import au.gov.dva.sopref.interfaces.model.{Factor, SoP}

trait SoPParser {
  def parseFactorTextToParagraphs(factorsSection : String) : Map[String,String]

}
















