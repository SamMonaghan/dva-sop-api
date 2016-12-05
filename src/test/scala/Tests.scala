
import au.gov.dva.sopref.interfaces.model.StandardOfProof
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
    val undertest = LsParser
    val result = undertest.parseAll(undertest.singleFactorParser, testInput)
    System.out.print(result)
  }

  test("Parse several factors") {
    val testinput = "(a) being a prisoner of war before the clinical onset of lumbar spondylosis; or (b) having inflammatory joint disease in the lumbar spine before the clinical onset of lumbar spondylosis; or (c) having an infection of the affected joint as specified at least one year before the clinical onset of lumbar spondylosis; or (d) having an intra-articular fracture of the lumbar spine at least one year before the clinical onset of lumbar spondylosis; or (e) having a specified spinal condition affecting the lumbar spine for at least the one year before the clinical onset of lumbar spondylosis; or (f) having leg length inequality for at least the two years before the clinical onset of lumbar spondylosis; or (g) having a depositional joint disease in the lumbar spine before the clinical onset of lumbar spondylosis; or ";

    val underTest =  LsParser
    val result = underTest.parseAll(underTest.factorListParser, testinput)
    System.out.print(result)
    assert(result.successful && result.get.size == 7)
  }

  test("Parse head and factors") {
    val testinput = "The factor that must as a minimum exist before it can be said that a reasonable hypothesis has been raised connecting lumbar spondylosis or death from lumbar spondylosis with the circumstances of a person’s relevant service is: (a) being a prisoner of war before the clinical onset of lumbar spondylosis; or (b) having inflammatory joint disease in the lumbar spine before the clinical onset of lumbar spondylosis; or (c) having an infection of the affected joint as specified at least one year before the clinical onset of lumbar spondylosis; or "

    val underTest = LsParser
    val result = underTest.parseAll(underTest.headAndFactorsParser, testinput)

    System.out.print(result)
    assert(result.successful && result.get._2.size == 3)

  }



  test("Parse all factors from Lumbar Spondylosis"){
    val testInput = Source.fromInputStream(getClass().getResourceAsStream("lsExtractedFactorsText.txt"),"UTF-8").mkString;
    val underTest = LsParser;
    val result = underTest.parseAll(underTest.completeFactorSectionParser,testInput)
    System.out.print(result)
    assert(result.successful)
     assert(result.get._2.size == 32)
  }

  test("Ls parser implements interface correctly") {

    val testInput = Source.fromInputStream(getClass().getResourceAsStream("lsExtractedFactorsText.txt"),"UTF-8").mkString;
    val underTest = LsParser;
    val result = underTest.parseFactors(testInput)
    assert(result._1 == StandardOfProof.ReasonableHypothesis)
    assert(result._2.size == 32)
  }

  test("Parse instrument number") {
    val testInput = "This Instrument may be cited as Statement of Principles concerning lumbar spondylosis No. 62 of 2014."
    val result = LsParser.parseInstrumentNumber(testInput)
    assert(result.getNumber == 62 && result.getYear == 2014)

  }

  test("Parse simple definition") {
    val testinput = "\"ICD-10-AM code\" means a number assigned to a particular kind of injury or\ndisease in The International Statistical Classification of Diseases and Related\nHealth Problems, 10th Revision, Australian Modification (ICD-10-AM),\nEighth Edition, effective date of 1 July 2013, copyrighted by the Independent\nHospital Pricing Authority, and having ISBN 978-1-74128-213-9;"

    val result = LsParser.parseAll(LsParser.simpleDefinitionParser,testinput)
    assert(result.successful && result.get._1 == "ICD-10-AM code" && result.get._2.endsWith("74128-213-9"))

  }

  test("Parse defined word") {
    val testinput = "\"ICD-10-AM code\" means"
    val result = LsParser.parseAll(LsParser.definedWordParser,testinput)
    assert(result.successful)

  }

  test("Parse simple definition body with ; terminator") {
    val testinput = "a number assigned to a particular kind of injury or disease in The International Statistical Classification of Diseases and Related Health Problems, 10th Revision, Australian Modification (ICD-10-AM), Eighth Edition, effective date of 1 July 20.13, copyrighted by the Independent Hospital Pricing Authority, and having ISBN 978-1-74128-213-9;"

    val result = LsParser.parseAll(LsParser.simpleWordMeaningParser,testinput)
    assert(result.successful && result.get.endsWith("-9"))
  }
  test("Parse simple definition body with . terminator") {
    val testinput = "a number assigned to a particular kind of injury or disease in The International Statistical Classification of Diseases and Related Health Problems, 10th Revision, Australian Modification (ICD-10-AM), Eighth Edition, effective date of 1 July 20.13, copyrighted by the Independent Hospital Pricing Authority, and having ISBN 978-1-74128-213-9."

    val result = LsParser.parseAll(LsParser.simpleWordMeaningParser,testinput)
    assert(result.successful && result.get.endsWith("-9"))
  }





}


