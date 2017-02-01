
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
    val result = PreAugust2015Extractor.extractFactorSection(ParserTestUtils.resourceToString("osteoarthritisCleansedText.txt"))
    println(result)
    assert(result != null)
  }

  test("Separated factor list parser works on sub paras without or terminator") {
    val input = ParserTestUtils.resourceToString("subparasOnlyText.txt");
    val result = PreAugust2015Parser.parseAll(PreAugust2015Parser.separatedFactorListParser, input);
    println(result)
    assert(result.successful)
  }

  test("Separated factor list parser works on ordinary paras without or terminator") {
    val input = "(a) being a prisoner of war before the clinical onset of osteoarthritis;\nor\n(b) having inflammatory joint disease of the affected joint before the\nclinical onset of osteoarthritis in that joint"
    val result = PreAugust2015Parser.parseAll(PreAugust2015Parser.separatedFactorListParser, input);
    println(result)
    assert(result.successful)
  }

  test("Para with sub paras parses and no tail") {
    val input = "(l) for osteoarthritis of a joint of the lower limb only, (i) having an amputation involving either leg; or (ii) having an asymmetric gait"

    val result = OsteoarthritisParser.parseAll(OsteoarthritisParser.completeFactorWithSubParasParser, input)

    assert(result.successful)
    println(result)
  }


  test("Para with sub paras and tail") {
    val input = "(l) for osteoarthritis of a joint of the lower limb only, (i) having an amputation involving either leg; or (ii) having an asymmetric gait; for at least three years before the clinical onset of osteoarthritis in that joint"

    val result = OsteoarthritisParser.parseAll(OsteoarthritisParser.completeFactorWithSubParasParser, input)

    assert(result.successful)
    println(result)
  }

  test("Para n")
  {
    val input = "(n) for osteoarthritis of a joint of the lower limb or hand joint only, (i) being overweight for at least 10 years before the clinical onset of osteoarthritis in that joint; or (ii) for males, having a waist to hip circumference ratio exceeding 1.0 for at least 10 years, before the clinical onset of osteoarthritis in that joint; or (iii) for females, having a waist to hip circumference ratio exceeding 0.9 for at least 10 years, before the clinical onset of osteoarthritis in that joint"

    val result = OsteoarthritisParser.parseAll(OsteoarthritisParser.completeFactorWithSubParasParser,input)

    assert(result.successful)
    println(result)
  }

}
