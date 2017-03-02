
package au.gov.dva.sopapi.tests.parsertests

import au.gov.dva.sopapi.sopref.parsing.traits
import au.gov.dva.dvasopapi.tests.TestUtils
import au.gov.dva.sopapi.sopref.data.sops.StoredSop
import au.gov.dva.sopapi.sopref.parsing.implementations.cleansers.GenericCleanser
import au.gov.dva.sopapi.sopref.parsing.implementations.extractors.PreAugust2015Extractor
import au.gov.dva.sopapi.sopref.parsing.implementations.parsers.{OsteoarthritisParser, PreAugust2015Parser}
import au.gov.dva.sopapi.tests.parsers.ParserTestUtils
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class OsteoarthritisTests extends FunSuite {

  test("Parse entire RH Osteoarthritis SoP") {
    val result = ParserTestUtils.executeWholeParsingPipeline("F2011C00491", "sops_rh/F2011C00491.pdf")
    System.out.print(TestUtils.prettyPrint(StoredSop.toJson(result)))
    assert(result != null)
  }

  ignore("Parse entire BoP Osteoarthritis SoP") {
    val result = ParserTestUtils.executeWholeParsingPipeline("F2011C00492", "sops_bop/F2011C00492.pdf")
    System.out.println(TestUtils.prettyPrint(StoredSop.toJson(result)))
    assert(result != null)
  }

  test("Cleanse osteoarthritis text for RH") {
    val result = ParserTestUtils.produceCleansedText("F2011C00491", "sops_rh/F2011C00491.pdf")
    println(result)
    assert(result != null)
  }

  test("Extract factors section from RH cleansed text") {
    val result = PreAugust2015Extractor.extractFactorsSection(ParserTestUtils.resourceToString("osteoarthritisCleansedText.txt"))
    println(result)
    assert(result != null)
  }


  test("Para with sub paras parses and no tail") {
    val input = "(l) for osteoarthritis of a joint of the lower limb only, (i) having an amputation involving either leg; or (ii) having an asymmetric gait"

    val result = OsteoarthritisParser.parseAll(OsteoarthritisParser.completeFactorWithSubParas, input)

    assert(result.successful)
    println(result)
  }


  test("Para with sub paras and tail") {
    val input = "(l) for osteoarthritis of a joint of the lower limb only, (i) having an amputation involving either leg; or (ii) having an asymmetric gait; for at least three years before the clinical onset of osteoarthritis in that joint"

    val result = OsteoarthritisParser.parseAll(OsteoarthritisParser.completeFactorWithSubParas, input)

    assert(result.successful)
    println(result)
  }

  test("Para with sub paras and tail to object") {
    val input = "(l) for osteoarthritis of a joint of the lower limb only, (i) having an amputation involving either leg; or (ii) having an asymmetric gait; for at least three years before the clinical onset of osteoarthritis in that joint"

    val result = OsteoarthritisParser.parseAll(OsteoarthritisParser.factor, input)

    assert(result.successful)
    println(result)
  }


  test("Para n")
  {
    val input = "(n) for osteoarthritis of a joint of the lower limb or hand joint only, (i) being overweight for at least 10 years before the clinical onset of osteoarthritis in that joint; or (ii) for males, having a waist to hip circumference ratio exceeding 1.0 for at least 10 years, before the clinical onset of osteoarthritis in that joint; or (iii) for females, having a waist to hip circumference ratio exceeding 0.9 for at least 10 years, before the clinical onset of osteoarthritis in that joint"

    val result = OsteoarthritisParser.parseAll(OsteoarthritisParser.twoLevelPara,input)

    println(result)
    assert(result.successful)
  }

  test("Ordinary single para factor works with parser that can also parse two level factors") {
    val input = "(a) being a prisoner of war before the clinical onset of osteoarthritis"
    val result = OsteoarthritisParser.parseAll(OsteoarthritisParser.factor,input)
    println(result)
    assert(result.successful)
  }

  test("Another two level factor")
  {
    val input = "(k) for osteoarthritis of a joint of the upper limb only, (i) performing any combination of repetitive activities or forceful activities for an average of at least 30 hours per week, for a continuous period of at least ten years before the clinical onset of osteoarthritis in that joint; or (i) using a hand-held, vibrating, percussive, industrial tool on more days than not, for at least 10 years before the clinical onset of osteoarthritis in that joint"

    val result = OsteoarthritisParser.parseAll(OsteoarthritisParser.twoLevelPara,input)
    println(result)
    assert(result.successful)
  }

}
