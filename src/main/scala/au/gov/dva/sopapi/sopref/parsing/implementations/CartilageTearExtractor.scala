package au.gov.dva.sopapi.sopref.parsing.implementations

import au.gov.dva.sopapi.interfaces.model.ICDCode
import au.gov.dva.sopapi.sopref.parsing.traits.{PreAugust2015SoPExtractor}

class CartilageTearExtractor extends PreAugust2015SoPExtractor {

  // No ICD codes in F2010L01666 and F2010L01667
  override def extractICDCodes(plainTextSop: String): List[ICDCode] = {
    val individualsCodes = Nil
    individualsCodes.toList
  }

}
