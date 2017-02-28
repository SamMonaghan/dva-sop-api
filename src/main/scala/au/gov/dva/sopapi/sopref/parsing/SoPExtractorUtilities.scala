package au.gov.dva.sopapi.sopref.parsing

import au.gov.dva.sopapi.exceptions.SopParserError

import scala.collection.immutable.{IndexedSeq, Seq}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex


object SoPExtractorUtilities {

  def getSections(cleansedSoPText: String, sectionHeaderLineRegex: Regex): List[List[String]] = {
    val acc = List[List[String]]();
    val lines = cleansedSoPText.split("[\r\n]+").toList // todo: change to platform indep line sep
    divideRecursive(List.empty, sectionHeaderLineRegex, acc, lines)
  }

  private def divideRecursive(nextSectionTitle: List[String], sectionHeaderLineRegex: Regex, acc: List[List[String]], remaining: List[String]): List[List[String]] = {
    if (remaining.isEmpty)
      acc
    else {
      val sectionLinesWithTitleForNextHeading: List[String] = remaining.head :: remaining.tail.takeWhile(l => sectionHeaderLineRegex.findFirstMatchIn(l).isEmpty)
      val headerForNextSection = sectionLinesWithTitleForNextHeading.takeRight(1).map(i => i.trim)
      val sectionLinesWithoutTitleForNextSection = sectionLinesWithTitleForNextHeading.dropRight(1)
      val sectionLines = (nextSectionTitle ++ sectionLinesWithoutTitleForNextSection).map(l => l.trim)
      divideRecursive(headerForNextSection, sectionHeaderLineRegex, sectionLines :: acc, remaining.drop(sectionLinesWithTitleForNextHeading.size))
    }
  }

  def parseSectionBlock(sectionBlock: List[String]): (Option[Int], String, List[String]) = {
    val lines = sectionBlock
    val title: String = lines.head
    val numberedLine: String = lines.tail(0)
    val sectionHeaderLineRegex = """^([0-9]+)\.\s""".r
    val m = sectionHeaderLineRegex.findFirstMatchIn(numberedLine)
    if (m.isDefined) {
      val sectionNumber = m.get.group(1).toInt
      val bodyTextWithoutSectionNumber: List[String] = List(sectionHeaderLineRegex.replaceFirstIn(numberedLine, "")) ++ lines.tail.drop(1)
      return (Some(sectionNumber), title, bodyTextWithoutSectionNumber)
    }
    else {
      return (None, title, lines.tail)
    }
  }


  def getSection(cleansedSopText: String, paragraphLineRegex: Regex): (Int, List[String]) = {
    val sectionHeaderLineRegex = """^([0-9]+)\.\s""".r
    val allSections = getSections(cleansedSopText, sectionHeaderLineRegex).map(s => parseSectionBlock(s))

    val sectionForSpecifiedPara = allSections.find(s => paragraphLineRegex.findFirstIn(s._2).nonEmpty)
    if (sectionForSpecifiedPara.isEmpty)
      throw new SopParserError("No section found with title matching regex: " + paragraphLineRegex.regex)

    if (sectionForSpecifiedPara.get._1.isEmpty)
      throw new SopParserError("Could not determine section number using regex: " + sectionHeaderLineRegex.regex)

    (sectionForSpecifiedPara.get._1.get, sectionForSpecifiedPara.get._3)
  }


  def getMainParaLetterSequence = {
    val aToz = 'a' to 'z'
    val doubled = aToz.map(l => s"$l$l")
    val combined = aToz ++ doubled
    combined.map(i => "(" + i + ")")
  }

  def splitFactorsSectionByFactor(factorsSection: List[String]): List[List[String]] = {

    val lettersSequence = getMainParaLetterSequence.toList
    // skip the head
    val sectionWithoutHead = factorsSection.dropWhile(l => !l.startsWith("("));
    divideFactorSectionRecursive(lettersSequence, 1, List.empty, sectionWithoutHead).reverse

  }

  def divideFactorSectionRecursive(lettersSequence: List[String], nextLetterIndex: Int, acc: List[List[String]], remainingLines: List[String]): List[List[String]] = {
    if (remainingLines.isEmpty)
      acc
    else {
      val nextLetter = lettersSequence(nextLetterIndex)

      val factorLines = remainingLines.takeWhile(l => !l.startsWith(nextLetter)) // todo: what if a letter is missing, eg repealed?
      divideFactorSectionRecursive(lettersSequence, nextLetterIndex + 1, factorLines :: acc, remainingLines.drop(factorLines.size))
    }
  }

  // edge case is where h para has sub para beginning with i
  // can tell this because first line of h para ends in ,

}


