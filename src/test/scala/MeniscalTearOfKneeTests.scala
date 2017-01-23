
package au.gov.dva.sopapi.tests.parsers;

import java.time.LocalDate

import au.gov.dva.dvasopapi.tests.TestUtils
import au.gov.dva.sopapi.dtos.StandardOfProof
import au.gov.dva.sopapi.sopref.data.sops.{BasicICDCode, StoredSop}
import au.gov.dva.sopapi.sopref.parsing.implementations.ParsedFactor
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class MeniscalTearOfKneeTests extends FunSuite {

  val rhFixture = new {
    val result = ParserTestUtils.executeWholeParsingPipeline("F2010L01668", "sops_rh/F2010L01668.pdf")
  }

  val bopFixture = new {
    val result = ParserTestUtils.executeWholeParsingPipeline("F2010L01669", "sops_bop/F2010L01669.pdf")
  }

  test("Parse entire RH acute meniscal tear of the knee SoP") {
    System.out.print(TestUtils.prettyPrint(StoredSop.toJson(rhFixture.result)))
    assert(rhFixture.result != null)
  }

  test("Parse RH acute meniscal tear of the knee register ID") {
    assert(rhFixture.result.getRegisterId === "F2010L01668")
  }

  test("Parse RH acute meniscal tear of the knee instrument number") {
    assert(rhFixture.result.getInstrumentNumber.getNumber === 55)
    assert(rhFixture.result.getInstrumentNumber.getYear === 2010)
  }

  test("Parse RH acute meniscal tear of the knee citation") {
    assert(rhFixture.result.getCitation === "Statement of Principles concerning " +
      "acute meniscal tear of the knee No. 55 of 2010")
  }

  test("Parse RH acute meniscal tear of the knee condition name") {
    assert(rhFixture.result.getConditionName === "acute meniscal tear of the knee")
  }

  test("Parse RH acute meniscal tear of the knee effective from date") {
    assert(rhFixture.result.getEffectiveFromDate === LocalDate.of(2010, 6, 30))
  }

  test("Parse RH acute meniscal tear of the knee standard of proof") {
    assert(rhFixture.result.getStandardOfProof === StandardOfProof.ReasonableHypothesis)
  }

  // ICD codes
  test("Parse RH acute meniscal tear of the knee ICD codes") {
    assert(rhFixture.result.getICDCodes.contains(new BasicICDCode("ICD-10-AM", "S83.2")))
  }

  // Onset factors
  test("Parse RH acute meniscal tear of the knee onset factors") {
    val a = new ParsedFactor("6(a)",
      "having a significant physical force applied to or through the affected knee joint at " +
        "the time of the clinical onset of acute meniscal tear of the knee",
      Nil, Nil.toSet)

    assert(rhFixture.result.getOnsetFactors.contains(a))
  }

  // Aggravation factors
  test("Parse RH acute meniscal tear of the knee aggravation factors") {
    val b = new ParsedFactor("6(b)",
      "inability to obtain appropriate clinical management for acute meniscal tear of the knee",
      Nil, Nil.toSet)

    assert(rhFixture.result.getOnsetFactors.contains(b))
  }

  test("Parse entire BoP acute meniscal tear of the knee SoP") {
    System.out.println(TestUtils.prettyPrint(StoredSop.toJson(bopFixture.result)))
    assert(bopFixture.result != null)
  }

  test("Parse BoP acute meniscal tear of the knee register ID") {
    assert(bopFixture.result.getRegisterId === "F2010L01669")
  }

  test("Parse BoP acute meniscal tear of the knee instrument number") {
    assert(bopFixture.result.getInstrumentNumber.getNumber === 56)
    assert(bopFixture.result.getInstrumentNumber.getYear === 2010)
  }

  test("Parse BoP acute meniscal tear of the knee citation") {
    assert(bopFixture.result.getCitation === "Statement of Principles concerning " +
      "acute meniscal tear of the knee No. 56 of 2010")
  }

  test("Parse BoP acute meniscal tear of the knee condition name") {
    assert(bopFixture.result.getConditionName === "acute meniscal tear of the knee")
  }

  test("Parse BoP acute meniscal tear of the knee effective from date") {
    assert(bopFixture.result.getEffectiveFromDate === LocalDate.of(2010, 6, 30))
  }

  test("Parse BoP acute meniscal tear of the knee standard of proof") {
    assert(bopFixture.result.getStandardOfProof === StandardOfProof.BalanceOfProbabilities)
  }

  // ICD codes
  test("Parse BoP acute meniscal tear of the knee ICD codes") {
    assert(bopFixture.result.getICDCodes.contains(new BasicICDCode("ICD-10-AM", "S83.2")))
  }

  // Onset factors
  test("Parse BoP acute meniscal tear of the knee onset factors") {
    val a = new ParsedFactor("6(a)",
      "having a significant physical force applied to or through the affected " +
        "knee joint at the time of the clinical onset of acute meniscal tear of the knee",
      Nil, Nil.toSet)

    assert(bopFixture.result.getOnsetFactors.contains(a))
  }

  // Aggravation factors
  test("Parse BoP acute meniscal tear of the knee aggravation factors") {
    val b = new ParsedFactor("6(b)",
      "inability to obtain appropriate clinical management for acute meniscal tear of the knee",
      Nil, Nil.toSet)

    assert(bopFixture.result.getAggravationFactors.contains(b))
  }

}
