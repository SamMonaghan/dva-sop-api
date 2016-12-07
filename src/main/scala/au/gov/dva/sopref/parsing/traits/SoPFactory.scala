package au.gov.dva.sopref.parsing.traits

import au.gov.dva.sopref.interfaces.model.SoP

trait SoPFactory {
  def create(registerId : String, clensedText : String) : SoP

  def splitFactors(parasInOrder : List[String], startPara : String, endPara : String) = {
    val onsetParas = parasInOrder.takeWhile(i => i != startPara) ++ parasInOrder.reverse.takeWhile(i => i != endPara)
    val aggParas = parasInOrder.filter(p => !onsetParas.contains(p))
    (onsetParas,aggParas)
  }
}
