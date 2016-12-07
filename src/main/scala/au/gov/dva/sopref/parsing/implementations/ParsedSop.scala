package au.gov.dva.sopref.parsing.implementations

import java.time.LocalDate

import au.gov.dva.sopref.interfaces.model.{Factor, InstrumentNumber, SoP, StandardOfProof}
import com.google.common.collect.{ImmutableList, ImmutableSet}

class ParsedSop(registerId: String, instrumentNumber: InstrumentNumber, citation: String, aggravationFactors: List[Factor],
                onsetFactors: List[Factor], effectiveFromDate : LocalDate, standardOfProof: StandardOfProof) extends SoP
{

  override def getRegisterId: String = registerId

  override def getInstrumentNumber: InstrumentNumber = instrumentNumber

  override def getCitation: String = citation

  override def getAggravationFactors: ImmutableList[Factor] = ImmutableList.copyOf(aggravationFactors.toArray)

  override def getOnsetFactors: ImmutableList[Factor] = ImmutableList.copyOf(onsetFactors.toArray)

  override def getEffectiveFromDate: LocalDate = effectiveFromDate

  override def getStandardOfProof: StandardOfProof = standardOfProof
}
