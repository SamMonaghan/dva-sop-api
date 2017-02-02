package au.gov.dva.sopapi.sopref.parsing.traits
import au.gov.dva.sopapi.dtos.StandardOfProof
import au.gov.dva.sopapi.sopref.parsing.implementations.model.FactorInfo

trait FactorsParserForCaptalisedMainFactorParas extends FactorsParser{

  override def mainParaLetter : Parser[String] = """\(([A-Z]+)\)""".r

}
