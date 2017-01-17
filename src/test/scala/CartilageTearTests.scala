
package au.gov.dva.sopapi.tests.parsers;

import au.gov.dva.dvasopapi.tests.TestUtils
import au.gov.dva.sopapi.sopref.data.sops.StoredSop
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CartilageTearTests extends FunSuite {

  test("Parse entire RH Cartilage Tear SoP") {
      val result = ParserTestUtils.executeWholeParsingPipeline("F2010L01666", "sops_rh/F2010L01666.pdf")
      System.out.print(TestUtils.prettyPrint(StoredSop.toJson(result)))
      assert(result != null)
  }

  test("Parse entire BoP Cartilage Tear SoP") {
    val result = ParserTestUtils.executeWholeParsingPipeline("F2010L01667", "sops_bop/F2010L01667.pdf")
    System.out.println(TestUtils.prettyPrint(StoredSop.toJson(result)))
    assert(result != null)
  }

}
