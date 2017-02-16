package au.gov.dva.sopapi.tests.parsertests

import au.gov.dva.sopapi.sopref.parsing.SoPExtractorUtilities
import au.gov.dva.sopapi.tests.parsers.ParserTestUtils
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PreAug2015Tests extends FunSuite{

    val BoPidsForPreAug2015 = List(
      "F2014L00932",
      "F2010L02319",
      "F2014L01786",
      "F2008L02195",
      "F2010L01051",
      "F2013L00021",
      "F2010L01665",
      "F2011L00766",
      "F2010L01049",
      "F2014L00930",
      "F2014L01145",
      "F2013L01133",
      "F2014L00929",
      "F2012L01364")

   val RHIdsForPreAug2015 = List(
      "F2014L00928",
      "F2010L02318",
      "F2008L02192",
      "F2010L01050",
      "F2013L00020",
      "F2010L01664",
      "F2011L00783",
      "F2010L01048",
      "F2014L00933",
      "F2011L01743",
      "F2014L01144",
      "F2013L01129",
      "F2014L00931",
      "F2012L00016",
      "F2012L01361"
    )

    test("Push all pre Aug BOP 2015 through pipeline")
     {
        BoPidsForPreAug2015.foreach(id => {
          println(id)
          val result = ParserTestUtils.executeWholeParsingPipeline(id, "sops_bop/" + id + ".pdf")
          assert(result != null)

        })

     }

  test("Push all pre Aug  2015 RH through pipeline")
  {
    RHIdsForPreAug2015.foreach(id => {
      println(id)
      val result = ParserTestUtils.executeWholeParsingPipeline(id, "sops_rh/" + id + ".pdf")
      assert(result != null)
    })

  }



  test("Main para capitaliser")
  {
    val input = ParserTestUtils.produceCleansedText("F2013L01129","sops_rh/F2013L01129.pdf")
    val factorsSection: (Int, List[String]) = SoPExtractorUtilities.getSection(input, """^Factors$""".r)


    val factorsSectionLines = factorsSection._2
    val result = SoPExtractorUtilities.capitaliseMainFactorParaLetters(factorsSectionLines)
    println(result.mkString(" "))
  }







}