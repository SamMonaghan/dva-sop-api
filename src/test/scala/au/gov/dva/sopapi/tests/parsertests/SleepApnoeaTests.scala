package au.gov.dva.sopapi.tests.parsertests

import au.gov.dva.sopapi.sopref.parsing.traits.{FactorsParserForCaptalisedMainFactorParas, PreAugust2015SoPParser}
import au.gov.dva.sopapi.tests.parsers.ParserTestUtils
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SleepApnoeaTests extends FunSuite {



  object underTest extends FactorsParserForCaptalisedMainFactorParas

  test("Parse factors section")
  {
    val input = ParserTestUtils.resourceToString("sleepApnoeaCapFactors.txt")

    val result = underTest.parseFactorsSection(input)
    println(result)

  }

}
