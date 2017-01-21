
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

  // ICD codes
  test("Parse RH ICD codes") {
    assert(rhFixture.result.getICDCodes.contains(new BasicICDCode("ICD-10-AM", "M47.16")))
    assert(rhFixture.result.getICDCodes.contains(new BasicICDCode("ICD-10-AM", "M47.17")))
    assert(rhFixture.result.getICDCodes.contains(new BasicICDCode("ICD-10-AM", "M47.26")))
    assert(rhFixture.result.getICDCodes.contains(new BasicICDCode("ICD-10-AM", "M47.27")))
    assert(rhFixture.result.getICDCodes.contains(new BasicICDCode("ICD-10-AM", "M47.86")))
    assert(rhFixture.result.getICDCodes.contains(new BasicICDCode("ICD-10-AM", "M47.87")))
    assert(rhFixture.result.getICDCodes.contains(new BasicICDCode("ICD-10-AM", "M47.96")))
    assert(rhFixture.result.getICDCodes.contains(new BasicICDCode("ICD-10-AM", "M47.97")))
    assert(rhFixture.result.getICDCodes.contains(new BasicICDCode("ICD-10-AM", "M51.3")))
  }

  // Onset factors
  test("Parse RH onset factors") {
    val a = new ParsedFactor("6(a)",
      "being a prisoner of war before the clinical onset of lumbar spondylosis",
      Nil.toList, Nil.toSet)

    val b = new ParsedFactor("6(b)",
      "having inflammatory joint disease in the lumbar spine before the clinical " +
        "onset of lumbar spondylosis",
      Nil.toList, Nil.toSet)

    val c = new ParsedFactor("6(c)",
      "having an infection of the affected joint as specified at least one " +
        "year before the clinical onset of lumbar spondylosis",
      Nil.toList, Nil.toSet)

    val d = new ParsedFactor("6(d)",
      "having an intra-articular fracture of the lumbar spine at least one year " +
        "before the clinical onset of lumbar spondylosis",
      Nil.toList, Nil.toSet)

    val e = new ParsedFactor("6(e)",
      "having a specified spinal condition affecting the lumbar spine for at least " +
        "the one year before the clinical onset of lumbar spondylosis",
      Nil.toList, Nil.toSet)

    val f = new ParsedFactor("6(f)",
      "having leg length inequality for at least the two years before the clinical " +
        "onset of lumbar spondylosis",
      Nil.toList, Nil.toSet)

    val g = new ParsedFactor("6(g)",
      "having a depositional joint disease in the lumbar spine before the clinical " +
        "onset of lumbar spondylosis",
      Nil.toList, Nil.toSet)

    val h = new ParsedFactor("6(h)",
      "having trauma to the lumbar spine at least one year before the clinical onset " +
        "of lumbar spondylosis",
      Nil.toList, Nil.toSet)

    val i = new ParsedFactor("6(i)",
      "having a lumbar intervertebral disc prolapse before the clinical onset of " +
        "lumbar spondylosis at the level of the intervertebral disc prolapse",
      Nil.toList, Nil.toSet)

    val j = new ParsedFactor("6(j)",
      "lifting loads of at least 25 kilograms while bearing weight through the lumbar " +
        "spine to a cumulative total of at least 120 000 kilograms within any ten year " +
        "period before the clinical onset of lumbar spondylosis",
      Nil.toList, Nil.toSet)

    val k = new ParsedFactor("6(k)",
      "carrying loads of at least 25 kilograms while bearing weight through the lumbar " +
        "spine to a cumulative total of at least 3 800 hours within any ten year period " +
        "before the clinical onset of lumbar spondylosis",
      Nil.toList, Nil.toSet)

    val l = new ParsedFactor("6(l)",
      "being obese for at least ten years before the clinical onset of lumbar spondylosis",
      Nil.toList, Nil.toSet)

    val m = new ParsedFactor("6(m)",
      "flying in a powered aircraft as operational aircrew, for a cumulative total of at " +
        "least 1 000 hours within the 25 years before the clinical onset of lumbar spondylosis",
      Nil.toList, Nil.toSet)

    val n = new ParsedFactor("6(n)",
      "extreme forward flexion of the lumbar spine for a cumulative total of at least " +
        "1 500 hours before the clinical onset of lumbar spondylosis",
      Nil.toList, Nil.toSet)

    val o = new ParsedFactor("6(o)",
      "having acromegaly involving the lumbar spine before the clinical onset of lumbar spondylosis",
      Nil.toList, Nil.toSet)

    val p = new ParsedFactor("6(p)",
      "having Paget's disease of bone involving the lumbar spine before the clinical onset of lumbar spondylosis",
      Nil.toList, Nil.toSet)

    assert(rhFixture.result.getOnsetFactors.contains(a))
    assert(rhFixture.result.getOnsetFactors.contains(b))
    assert(rhFixture.result.getOnsetFactors.contains(c))
    assert(rhFixture.result.getOnsetFactors.contains(d))
    assert(rhFixture.result.getOnsetFactors.contains(e))
    assert(rhFixture.result.getOnsetFactors.contains(f))
    assert(rhFixture.result.getOnsetFactors.contains(g))
    assert(rhFixture.result.getOnsetFactors.contains(h))
    assert(rhFixture.result.getOnsetFactors.contains(i))
    assert(rhFixture.result.getOnsetFactors.contains(j))
    assert(rhFixture.result.getOnsetFactors.contains(k))
    assert(rhFixture.result.getOnsetFactors.contains(l))
    assert(rhFixture.result.getOnsetFactors.contains(m))
    assert(rhFixture.result.getOnsetFactors.contains(n))
    assert(rhFixture.result.getOnsetFactors.contains(o))
    assert(rhFixture.result.getOnsetFactors.contains(p))
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

  // ICD codes
  test("Parse BoP ICD codes") {
    assert(bopFixture.result.getICDCodes.contains(new BasicICDCode("ICD-10-AM", "M47.16")))
    assert(bopFixture.result.getICDCodes.contains(new BasicICDCode("ICD-10-AM", "M47.17")))
    assert(bopFixture.result.getICDCodes.contains(new BasicICDCode("ICD-10-AM", "M47.26")))
    assert(bopFixture.result.getICDCodes.contains(new BasicICDCode("ICD-10-AM", "M47.27")))
    assert(bopFixture.result.getICDCodes.contains(new BasicICDCode("ICD-10-AM", "M47.86")))
    assert(bopFixture.result.getICDCodes.contains(new BasicICDCode("ICD-10-AM", "M47.87")))
    assert(bopFixture.result.getICDCodes.contains(new BasicICDCode("ICD-10-AM", "M47.96")))
    assert(bopFixture.result.getICDCodes.contains(new BasicICDCode("ICD-10-AM", "M47.97")))
    assert(bopFixture.result.getICDCodes.contains(new BasicICDCode("ICD-10-AM", "M51.3")))
  }

  // Onset factors
  test("Parse BoP onset factors") {
    val a = new ParsedFactor("6(a)",
      "having inflammatory joint disease in the lumbar spine before the clinical " +
        "onset of lumbar spondylosis",
      Nil.toList, Nil.toSet)

    val b = new ParsedFactor("6(b)",
      "having an infection of the affected joint as specified at least one year " +
        "before the clinical onset of lumbar spondylosis",
      Nil.toList, Nil.toSet)

    val c = new ParsedFactor("6(c)",
      "having an intra-articular fracture of the lumbar spine at least one year " +
        "before the clinical onset of lumbar spondylosis",
      Nil.toList, Nil.toSet)

    val d = new ParsedFactor("6(d)",
      "having a specified spinal condition affecting the lumbar spine for at " +
        "least the one year before the clinical onset of lumbar spondylosis",
      Nil.toList, Nil.toSet)

    val e = new ParsedFactor("6(e)",
      "having leg length inequality for at least the five years before the clinical " +
        "onset of lumbar spondylosiss",
      Nil.toList, Nil.toSet)

    val f = new ParsedFactor("6(f)",
      "having a depositional joint disease in the lumbar spine before the clinical " +
        "onset of lumbar spondylosis",
      Nil.toList, Nil.toSet)

    val g = new ParsedFactor("6(g)",
      "having trauma to the lumbar spine at least one year before the clinical onset " +
        "of lumbar spondylosis, and where the trauma to the lumbar spine occurred " +
        "within the 25 years before the clinical onset of lumbar spondylosis",
      Nil.toList, Nil.toSet)

    val h = new ParsedFactor("6(h)",
      "having a lumbar intervertebral disc prolapse before the clinical onset of lumbar " +
        "spondylosis at the level of the intervertebral disc prolapse",
      Nil.toList, Nil.toSet)

    val i = new ParsedFactor("6(i)",
      "lifting loads of at least 35 kilograms while bearing weight through the lumbar " +
        "spine to a cumulative total of at least 168 000 kilograms within any ten year " +
        "period before the clinical onset of lumbar spondylosis, and where the clinical " +
        "onset of lumbar spondylosis occurs within the 25 years following that period",
      Nil.toList, Nil.toSet)

    val j = new ParsedFactor("6(j)",
      "carrying loads of at least 35 kilograms while bearing weight through the lumbar " +
        "spine to a cumulative total of at least 3 800 hours within any ten year period " +
        "before the clinical onset of lumbar spondylosis, and where the clinical onset " +
        "of lumbar spondylosis occurs within the 25 years following that period",
      Nil.toList, Nil.toSet)

    val k = new ParsedFactor("6(k)",
      "being obese for at least ten years within the 25 years before the clinical onset " +
        "of lumbar spondylosis",
      Nil.toList, Nil.toSet)

    val l = new ParsedFactor("6(l)",
      "flying in a powered aircraft as operational aircrew, for a cumulative total of at least 2 000 " +
        "hours within the 25 years before the clinical onset of lumbar spondylosis",
      Nil.toList, Nil.toSet)

    val m = new ParsedFactor("6(m)",
      "extreme forward flexion of the lumbar spine for a cumulative total of at least 1 500 hours " +
        "before the clinical onset of lumbar spondylosis",
      Nil.toList, Nil.toSet)

    val n = new ParsedFactor("6(n)",
      "having acromegaly involving the lumbar spine before the clinical onset of lumbar spondylosis",
      Nil.toList, Nil.toSet)

    val o = new ParsedFactor("6(o)",
      "having Paget's disease of bone involving the lumbar spine before the clinical onset of " +
        "lumbar spondylosis",
      Nil.toList, Nil.toSet)

    assert(bopFixture.result.getOnsetFactors.contains(a))
    assert(bopFixture.result.getOnsetFactors.contains(b))
    assert(bopFixture.result.getOnsetFactors.contains(c))
    assert(bopFixture.result.getOnsetFactors.contains(d))
    assert(bopFixture.result.getOnsetFactors.contains(e))
    assert(bopFixture.result.getOnsetFactors.contains(f))
    assert(bopFixture.result.getOnsetFactors.contains(g))
    assert(bopFixture.result.getOnsetFactors.contains(h))
    assert(bopFixture.result.getOnsetFactors.contains(i))
    assert(bopFixture.result.getOnsetFactors.contains(j))
    assert(bopFixture.result.getOnsetFactors.contains(k))
    assert(bopFixture.result.getOnsetFactors.contains(l))
    assert(bopFixture.result.getOnsetFactors.contains(m))
    assert(bopFixture.result.getOnsetFactors.contains(n))
    assert(bopFixture.result.getOnsetFactors.contains(o))
  }

}
