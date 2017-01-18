package au.gov.dva.sopapi.sopref.parsing.implementations

import au.gov.dva.sopapi.exceptions.SopParserError
import au.gov.dva.sopapi.sopref.parsing.traits.{PreAugust2015SoPParser}

object CartilageTearParser extends PreAugust2015SoPParser {

  override def parseStartAndEndAggravationParas(aggravationSection: String): (String, String) = {
    // Only one aggravation paragraph reference for F2010L01666 and F2010L01667
    val paraIntervalRegex = """Paragraph [0-9]+(\([a-z]+\))""".r
    val m = paraIntervalRegex.findFirstMatchIn(aggravationSection)
    if (m.isEmpty)
      throw new SopParserError("Cannot determine aggravation para from: " + aggravationSection)
    (m.get.group(1), m.get.group(1))
  }

}

