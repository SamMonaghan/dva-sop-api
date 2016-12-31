package au.gov.dva.sopapi.tests.parsers

import java.io.PrintWriter

import au.gov.dva.dvasopapi.tests.TestUtils
import au.gov.dva.sopapi.sopref.data.sops.StoredSop
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import scala.collection.mutable

@RunWith(classOf[JUnitRunner])
class BoPSoPParsingTests extends FunSuite {

  test("Parse all BoP SoPs") {
    val rhIds = ParserTestUtils.resourceToString("bopSopRegisterIds.txt").split("\n");

    val errorMap = mutable.HashMap.empty[String, Throwable];

    for (rhId <- rhIds) {

      try {
        val result = ParserTestUtils.executeWholeParsingPipeline(rhId, "sops_bop/" + rhId + ".pdf")

        if (result == null) {
          errorMap += (rhId -> null)
        }
      } catch {
        case e: Throwable => errorMap += (rhId -> e)
      }

    }

    val pw = new PrintWriter("bopParseFailures.txt")
    for (rhId <- errorMap.keySet) {
      System.out.println("FAILED " + rhId);
      pw.println("FAILED " + rhId)
      errorMap(rhId).printStackTrace(pw)
      pw.println()
    }

    if (!errorMap.isEmpty) {
      fail("Parse failures: " + errorMap.keySet.mkString(","))
    }
  }

}
