
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
    assert(result.successful)
  }

  test("Parse all factors from Lumbar Spondylosis"){
    val testInput = Source.fromInputStream(getClass().getResourceAsStream("lsExtractedFactorsText.txt")).mkString;
    val underTest = new LsParser();
    val result = underTest.parseFactorTextToParagraphs(testInput);
    assert(result.size == 32)
  }
}


