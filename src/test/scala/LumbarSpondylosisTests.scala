
package au.gov.dva.sopapi.tests.parsers;


import au.gov.dva.dvasopapi.tests.TestUtils
import au.gov.dva.sopapi.sopref.data.Conversions
import au.gov.dva.sopapi.sopref.data.sops.StoredSop
import au.gov.dva.sopapi.sopref.parsing.factories.SoPFactoryLocator
import au.gov.dva.sopapi.sopref.parsing.implementations.GenericClenser
import org.junit.Ignore
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner



@RunWith(classOf[JUnitRunner])
class LumbarSpondylosisTests extends FunSuite {

  test("CI debug")
  {
    val bytes = ParserTestUtils.resourceToBytes("sops_rh/F2014L00933.pdf")
    val rawText = Conversions.pdfToPlainText(bytes);
    val genericClenser = new GenericClenser();
    val clensedText = genericClenser.clense(rawText)
    // for some reason below returns n ull in CI only:
    val sopFactory = SoPFactoryLocator.findFactory("F2014L00933")
    //
    val sop = sopFactory.create("F2014L00933", clensedText)
    assert(sopFactory != null)
  }

  test("Test Scala case match statement for running in CI")
  {
    val f = (s : String)  => {

      s match {
        case "One" => "One"
        case "Two" => "Two"
        case _ => null
      }
    }

    assert(f("One") != null)
  }

  test("Parse entire RH LS SoP") {
      val result = ParserTestUtils.executeWholeParsingPipeline("F2014L00933", "sops_rh/F2014L00933.pdf")
      System.out.print(TestUtils.prettyPrint(StoredSop.toJson(result)))
      assert(result != null)
  }

  ignore("Parse entire BoP LS SoP")
  {
    val result = ParserTestUtils.executeWholeParsingPipeline("F2014L00930", "sops_bop/F2014L00930.pdf")
    System.out.println(TestUtils.prettyPrint(StoredSop.toJson(result)))
    assert(result != null)
  }


}
