package au.gov.dva.sopapi.tests.parsertests

import au.gov.dva.sopapi.sopref.parsing.SoPExtractorUtilities
import au.gov.dva.sopapi.sopref.parsing.traits.FactorsParser
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner


@RunWith(classOf[JUnitRunner])
class SingleFactorParsingTests extends FunSuite {

  object factorsParserUnderTest extends FactorsParser

  test("Parse single level para") {

    val input = "(r) having a disorder associated with loss of pain sensation or proprioception involving the affected joint before the clinical onset of osteoarthritis in that joint; or"
    val result = factorsParserUnderTest.parseAll(factorsParserUnderTest.factor, input)
    println(result)
    assert(result.successful)
  }

  test("Parse two level para") {
    val input = "(h) for obstructive sleep apnoea only, (i) having chronic obstruction or chronic narrowing of the upper airway at the time of the clinical worsening of sleep apnoea; or (ii) being obese at the time of the clinical worsening of sleep apnoea; or (iii) having hypothyroidism at the time of the clinical worsening of sleep apnoea; or (iv) having acromegaly at the time of the clinical worsening of sleep apnoea; or (v) being treated with antiretroviral therapy for human immunodeficiency virus infection before the clinical worsening of sleep apnoea; or"
    val result = factorsParserUnderTest.parseAll(factorsParserUnderTest.twoLevelPara, input)
    println(result)
    assert(result.successful)
  }

  test("Parse two level para with tail") {
    val input = "(ee) for osteoarthritis of a joint of the lower limb only, (i) having an amputation involving either leg; or (ii) having an asymmetric gait; for at least three years before the clinical worsening of osteoarthritis in that joint; or"
    val result = factorsParserUnderTest.parseAll(factorsParserUnderTest.factor, input)
    println(result)
    assert(result.successful)

  }

  test("Parse two level para without tail") {
    val input = "(k) for osteoarthritis of a joint of the upper limb only, (i) performing any combination of repetitive activities or forceful activities for an average of at least 30 hours per week, for a continuous period of at least ten years before the clinical onset of osteoarthritis in that joint; or (ii) using a hand-held, vibrating, percussive, industrial tool on more days than not, for at least 10 years before the clinical onset of osteoarthritis in that joint; or"

    val result = factorsParserUnderTest.parseAll(factorsParserUnderTest.twoLevelPara, input)
    println(result)
    assert(result.successful)
  }

  test("Parse two level para with colon after head") {
    val input = "(aa) having a fracture or dislocation to the distal radius, the distal ulna, a carpal bone or a metacarpal bone of the affected side which: (i) alters the normal contour of the carpal tunnel; or (ii) damages the flexor tendons within the carpal tunnel, before the clinical worsening of carpal tunnel syndrome; or"
    val result = factorsParserUnderTest.parseAll(factorsParserUnderTest.twoLevelPara, input)
    println(result)

  }

  test("Split head from rest for individual factor") {
    val input = "(b) for central sleep apnoea only,\n(i) having congestive cardiac failure at the time of the clinical\nonset of sleep apnoea; or\n(ii) using a long-acting opioid at an average daily morphine\nequivalent dose of at least 75 milligrams for at least the two\nmonths before the clinical onset of sleep apnoea; or"
    val inputSplitToLines = input.split("[\r\n]+").toList
    val result = SoPExtractorUtilities.splitFactorToHeaderAndRest(inputSplitToLines)
    assert(!result._2.isEmpty)
  }

  test("Parse single factor from text incl line breaks without tail") {

    val input = "(b) for central sleep apnoea only,\n(i) having congestive cardiac failure at the time of the clinical\nonset of sleep apnoea; or\n(ii) using a long-acting opioid at an average daily morphine\nequivalent dose of at least 75 milligrams for at least the two\nmonths before the clinical onset of sleep apnoea; or"
    val result = factorsParserUnderTest.parseSingleFactor(input)
    println(result)

  }

  test("Parse single factor from text incl line breaks and with tail") {
    val input = "(ee) for osteoarthritis of a joint of the lower limb only,\n(i) having an amputation involving either leg; or\n(ii) having an asymmetric gait;\nfor at least three years before the clinical worsening of\nosteoarthritis in that joint; or"
    val result = factorsParserUnderTest.parseSingleFactor(input)
    println(result)
  }

  test("Split tail from last factor") {
     val lastFactor = "(ii) having an asymmetric gait;\nfor at least three years before the clinical worsening of\nosteoarthritis in that joint; or";
      val result = factorsParserUnderTest.splitOutTailIfAny(lastFactor)

    println(result._2.get)
  }


}
