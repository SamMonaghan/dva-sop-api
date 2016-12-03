
import au.gov.dva.sopref.parsing.SoPExtractorUtilities._
import au.gov.dva.sopref.parsing._
import au.gov.dva.sopref.parsing.implementations.{GenericClenser, LsExtractor, LsParser}
import com.google.common.io.Resources
import org.scalatest.{FlatSpec, FunSuite}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import scala.io.Source

@RunWith(classOf[JUnitRunner])
class Tests extends FunSuite {
  test("example test") {
     val underTest = true;
    assert(underTest)
  }
}

@RunWith(classOf[JUnitRunner])
class ParserTests extends FunSuite {
  test("Clense LS raw text") {
    val sourceResourceStream = getClass().getResourceAsStream("lsConvertedToText.txt");
    val rawText = Source.fromInputStream(sourceResourceStream).mkString
    val lSClenser = new GenericClenser();
    val result = lSClenser.clense(rawText)

    assert(result.length() > 0)
    System.out.println("START:")
    System.out.print(result)
    System.out.println("END")
  }

  test("Extract Lumbar Spondylosis factors section from clensed text") {
    val testInput = Source.fromInputStream(getClass().getResourceAsStream("lsClensedText.txt")).mkString;
    val underTest = new LsExtractor()
    val result = underTest.extractFactorSection(testInput)
    System.out.print(result);
    assert(result._1 == 6)
  }

  test("Extract definition section for Lumbar Spondylosis") {
    val testInput = Source.fromInputStream(getClass().getResourceAsStream("lsClensedText.txt")).mkString;
    val underTest = new LsExtractor()
    val result = underTest.extractDefinitionsSection(testInput);
    assert(result.startsWith("For the purpose") && result.endsWith("surgery to the lumbar spine."))
    System.out.print(result)
  }

  test("Extract date of effect for Lumbar Spondylosis") {
    val testInput = Source.fromInputStream(getClass().getResourceAsStream("lsClensedText.txt")).mkString;
    val underTest = new LsExtractor()
    val result = underTest.extractDateOfEffectSection(testInput);
    assert(result == "This Instrument takes effect from 2 July 2014.");
  }

  test("Extract citation for Lumbar Spondylosis") {
    val testInput = Source.fromInputStream(getClass().getResourceAsStream("lsClensedText.txt")).mkString;
    val underTest = new LsExtractor()
    val result = underTest.extractCitation(testInput);
    assert(result == "This Instrument may be cited as Statement of Principles concerning lumbar spondylosis No. 62 of 2014.");
  }


  test("Extract ICD codes for Lumbar Spondylosis") {
    val testInput = Source.fromInputStream(getClass().getResourceAsStream("lsClensedText.txt")).mkString;
    val underTest = new LsExtractor()
    val result = underTest.extractICDCodes(testInput);
    result.foreach(c => System.out.print(c))
    assert(result.size == 9)
  }

  test("Parse single factor") {
    val testInput = "(a) being a prisoner of war before the clinical onset of lumbar spondylosis; or ";
    val undertest = new LsParser
    val result = undertest.parseAll(undertest.singleFactorParser, testInput)
    System.out.print(result)
  }

  test("Parse several factors") {
    val testinput = "(a) being a prisoner of war before the clinical onset of lumbar spondylosis; or (b) having inflammatory joint disease in the lumbar spine before the clinical onset of lumbar spondylosis; or (c) having an infection of the affected joint as specified at least one year before the clinical onset of lumbar spondylosis; or (d) having an intra-articular fracture of the lumbar spine at least one year before the clinical onset of lumbar spondylosis; or (e) having a specified spinal condition affecting the lumbar spine for at least the one year before the clinical onset of lumbar spondylosis; or (f) having leg length inequality for at least the two years before the clinical onset of lumbar spondylosis; or (g) having a depositional joint disease in the lumbar spine before the clinical onset of lumbar spondylosis; or ";

    val underTest = new LsParser
    val result = underTest.parseAll(underTest.factorListParser, testinput)
    System.out.print(result)
    assert(result.successful && result.get.size == 7)
  }

  test("Parse head and factors") {
    val testinput = "The factor that must as a minimum exist before it can be said that a reasonable hypothesis has been raised connecting lumbar spondylosis or death from lumbar spondylosis with the circumstances of a person’s relevant service is: (a) being a prisoner of war before the clinical onset of lumbar spondylosis; or (b) having inflammatory joint disease in the lumbar spine before the clinical onset of lumbar spondylosis; or (c) having an infection of the affected joint as specified at least one year before the clinical onset of lumbar spondylosis; or "

    val underTest = new LsParser
    val result = underTest.parseAll(underTest.headAndFactorsParser, testinput)

    System.out.print(result)
    assert(result.successful && result.get._2.size == 3)

  }


  test("Parse factor list with or as separator terminating in period.")
  {
    // note 25 changed to 25.0 to check periods parsed correctly in numbers
    val testinput = "(y) lifting loads of at least 25.0 kilograms while bearing weight through the lumbar spine to a cumulative total of at least 120 000 kilograms within any ten year period before the clinical worsening of lumbar spondylosis; or (z) carrying loads of at least 25 kilograms while bearing weight through the lumbar spine to a cumulative total of at least 3 800 hours within any ten year period before the clinical worsening of lumbar spondylosis; or (aa) being obese for at least ten years before the clinical worsening of lumbar spondylosis; or (bb) flying in a powered aircraft as operational aircrew, for a cumulative total of at least 1 000 hours within the 25 years before the clinical worsening of lumbar spondylosis; or (cc) extreme forward flexion of the lumbar spine for a cumulative total of at least 1 500 hours before the clinical worsening of lumbar spondylosis; or (dd) having acromegaly involving the lumbar spine before the clinical worsening of lumbar spondylosis; or (ee) having Paget's disease of bone involving the lumbar spine before the clinical worsening of lumbar spondylosis; or (ff) inability to obtain appropriate clinical management for lumbar spondylosis."

    val underTest = new LsParser
    val result = underTest.parseAll(underTest.separatedFactorListParser, testinput);
    System.out.print(result)
    assert(result.successful && result.get.size == 8)

  }

  test("Parse all factors from Lumbar Spondylosis"){
    val testInput = Source.fromInputStream(getClass().getResourceAsStream("lsExtractedFactorsText.txt")).mkString;
    val underTest = new LsParser();
    val result = underTest.parseFactorTextToParagraphs(testInput);
//     assert(result.size == 32)
  }
}


