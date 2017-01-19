
package au.gov.dva.sopapi.tests.parsers;

import java.time.LocalDate

import au.gov.dva.dvasopapi.tests.TestUtils
import au.gov.dva.sopapi.dtos.StandardOfProof
import au.gov.dva.sopapi.sopref.data.sops.StoredSop
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class LumbarSpondylosisTests extends FunSuite {

  val rhFixture = new {
    val result = ParserTestUtils.executeWholeParsingPipeline("F2014L00933", "sops_rh/F2014L00933.pdf")
  }

  val bopFixture = new {
    val result = ParserTestUtils.executeWholeParsingPipeline("F2014L00930", "sops_bop/F2014L00930.pdf")
  }

  test("Parse entire RH LS SoP") {
    System.out.print(TestUtils.prettyPrint(StoredSop.toJson(rhFixture.result)))
    assert(rhFixture.result != null)
  }

  test("Parse RH register ID") {
    assert(rhFixture.result.getRegisterId === "F2014L00933")
  }

  test("Parse RH instrument number") {
    assert(rhFixture.result.getInstrumentNumber.getNumber === 62)
    assert(rhFixture.result.getInstrumentNumber.getYear === 2014)
  }

  test("Parse RH citation") {
    assert(rhFixture.result.getCitation === "Statement of Principles concerning " +
      "lumbar spondylosis No. 62 of 2014")
  }

  test("Parse RH condition name") {
    assert(rhFixture.result.getConditionName === "lumbar spondylosis")
  }

  test("Parse RH effective from date") {
    assert(rhFixture.result.getEffectiveFromDate === LocalDate.of(2014, 7, 2))
  }

  test("Parse RH standard of proof") {
    assert(rhFixture.result.getStandardOfProof === StandardOfProof.ReasonableHypothesis)
  }

  test("Parse entire BoP LS SoP")
  {
    System.out.println(TestUtils.prettyPrint(StoredSop.toJson(bopFixture.result)))
    assert(bopFixture.result != null)
  }

  test("Parse BoP register ID") {
    assert(bopFixture.result.getRegisterId === "F2014L00930")
  }

  test("Parse BoP instrument number") {
    assert(bopFixture.result.getInstrumentNumber.getNumber === 63)
    assert(bopFixture.result.getInstrumentNumber.getYear === 2014)
  }

  test("Parse BoP citation") {
    assert(bopFixture.result.getCitation === "Statement of Principles concerning " +
      "lumbar spondylosis No. 63 of 2014")
  }

  test("Parse BoP condition name") {
    assert(bopFixture.result.getConditionName === "lumbar spondylosis")
  }

  test("Parse BoP effective from date") {
    assert(bopFixture.result.getEffectiveFromDate === LocalDate.of(2014, 7, 2))
  }

  test("Parse BoP standard of proof") {
    assert(bopFixture.result.getStandardOfProof === StandardOfProof.BalanceOfProbabilities)
  }

}
