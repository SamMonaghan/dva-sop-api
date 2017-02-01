
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

  test("Parse partial factors section")
  {
    val input = "The factor that must as a minimum exist before it can be said that a reasonable hypothesis has been raised connecting osteoarthritis or death from osteoarthritis with the circumstances of a person’s relevant service is: (a) being a prisoner of war before the clinical onset of osteoarthritis; or (b) having inflammatory joint disease of the affected joint before the clinical onset of osteoarthritis in that joint; or (c) having an infection of the affected joint as specified before the clinical onset of osteoarthritis in that joint; or (d) having an intra-articular fracture of the affected joint before the clinical onset of osteoarthritis in that joint; or (e) having haemarthrosis of the affected joint before the clinical onset of osteoarthritis in that joint; or (f) having a depositional joint disease in the affected joint before the clinical onset of osteoarthritis in that joint; or (g) having trauma to the affected joint before the clinical onset of osteoarthritis in that joint; or (h) having frostbite involving the affected joint before the clinical onset of osteoarthritis in that joint; or (i) having disordered joint mechanics of the affected joint for at least three years before the clinical onset of osteoarthritis in that joint; or (j) having necrosis of the subchondral bone near the affected joint before the clinical onset of osteoarthritis in that joint; or  (j) having necrosis of the subchondral bone near the affected joint before the clinical onset of osteoarthritis in that joint; or (k) for osteoarthritis of a joint of the upper limb only, (i) performing any combination of repetitive activities or forceful activities for an average of at least 30 hours per week, for a continuous period of at least ten years before the clinical onset of osteoarthritis in that joint; or (i) using a hand-held, vibrating, percussive, industrial tool on more days than not, for at least 10 years before the clinical onset of osteoarthritis in that joint; or (l) for osteoarthritis of a joint of the lower limb only, (i) having an amputation involving either leg; or (ii) having an asymmetric gait; for at least three years before the clinical onset of osteoarthritis in that joint."

    val result = OsteoarthritisParser.parseAll(OsteoarthritisParser.parseFactorSection,input)
    println(result)
    assert(result.successful)

  }

  test("Parse entire factors section")
  {
    val input = ParserTestUtils.resourceToString("osteoarthritis factors text.txt")
    val result = OsteoarthritisParser.parseAll(OsteoarthritisParser.parseFactorSection,input)
    println(result)
    assert(result.successful)
  }


}
