
package au.gov.dva.sopapi.tests.parsertests

import java.time.LocalDate

import au.gov.dva.dvasopapi.tests.TestUtils
import au.gov.dva.sopapi.dtos.StandardOfProof
import au.gov.dva.sopapi.interfaces.model.InstrumentNumber
import au.gov.dva.sopapi.sopref.data.sops.{BasicICDCode, StoredSop}
import au.gov.dva.sopapi.sopref.parsing.implementations.model.{ParsedDefinedTerm, ParsedFactor}
import au.gov.dva.sopapi.tests.parsers.ParserTestUtils
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SinusitisTests extends FunSuite {

  val rhFixture = new {
    val result = ParserTestUtils.executeWholeParsingPipeline("F2010L00553", "sops_rh/F2010L00553.pdf")
  }

  val bopFixture = new {
    val result = ParserTestUtils.executeWholeParsingPipeline("F2010L00554", "sops_bop/F2010L00554.pdf")
  }

  val radiationDef = new ParsedDefinedTerm("a course of therapeutic radiation",
    "means one or more fractions\n(treatment portions) of ionising radiation " +
      "administered with the aim of\nachieving palliation or cure with gamma rays, " +
      "x-rays, alpha particles or\nbeta particles")

  val dentalDef = new ParsedDefinedTerm("a specified dental condition",
    "means one of the following conditions:\n(a) endosseous implants;\n(b) infected dental " +
      "(apical or dentigerous) cyst;\n(c) non-vital tooth;\n(d) oro-antral fistula;\n" +
      "(e) periapical abscess;\n(f) periapical granuloma; or\n(g) periodontal disease")

  val substanceDef = new ParsedDefinedTerm("a specified substance",
    "means mustard gas, lewisite, ammonia gas,\nchlorine gas, sulphur dioxide, " +
      "nitrogen dioxide or cocaine")

  val respiratoryDef = new ParsedDefinedTerm("a viral respiratory tract infection",
    "means an acute infection of the\nrespiratory epithelium by a range of viruses " +
      "including rhinovirus,\ncoronavirus, influenza virus, causing such illnesses " +
      "as the common cold,\nlaryngotracheobronchitis, tracheitis, bronchitis and pneumonia")

  val nasalDef = new ParsedDefinedTerm("acute nasal symptoms or signs",
    "means:\n(a) rhinorrhea; or\n(b) irritation, inflammation, oedema, ulceration or " +
      "haemorrhage of\nthe nasal mucosa")

  val immunocompromisedDef = new ParsedDefinedTerm("an immunocompromised state",
    "means a state where the immune\nresponse has been attenuated by administration " +
      "of immunosuppressive\ndrugs, irradiation, malnutrition, a malignant disease " +
      "process or certain\ntypes of infection")

  val cigsPerDayDef = new ParsedDefinedTerm("cigarettes per day, or the equivalent " +
    "thereof in other tobacco products",
    "means either cigarettes, pipe tobacco or cigars, alone or in\nany combination " +
      "where one tailor made cigarette approximates one gram\nof tobacco; or one gram " +
      "of cigar, pipe or other smoking tobacco")

  val drainageDef = new ParsedDefinedTerm("impaired drainage of the sinus",
    "means one of the following which\nleads to a narrowing or obstruction of the " +
      "affected sinus or sinus\nopening:\n(a) an anatomical deformity including deviated " +
      "septum, enlarged\nturbinates, adenoidal hypertrophy, fracture of the facial bones " +
      "or\nany other bony structural abnormalities;\n(b) a soft tissue abnormality or " +
      "mucosal swelling affecting the sinus\nincluding polyps, tumours, inflammation, " +
      "sarcoidosis,\ngranulomas, or scarring; or\n(c) a foreign body including nasal " +
      "packing, nasogastric or\nnasotracheal tubes, or dental detritus")

  val cigsPackYearDef = new ParsedDefinedTerm("pack year of cigarettes, or the equivalent " +
    "thereof in other tobacco products",
    "means a calculation of consumption where one pack year of\ncigarettes equals twenty " +
      "tailor made cigarettes per day for a period of\none calendar year, or 7300 cigarettes.  " +
      "One tailor made cigarette\napproximates one gram of tobacco or one gram of cigar or pipe " +
      "tobacco\nby weight.  One pack year of tailor made cigarettes equates to 7300\ncigarettes, " +
      "or 7.3 kg of smoking tobacco by weight.  Tobacco products\nmeans either cigarettes, pipe " +
      "tobacco or cigars smoked, alone or in any\ncombination")

  test("Parse entire RH sinusitis SoP") {
    System.out.print(TestUtils.prettyPrint(StoredSop.toJson(rhFixture.result)))
    assert(rhFixture.result != null)
  }

  test("Parse RH sinusitis register ID") {
    assert(rhFixture.result.getRegisterId === "F2010L00553")
  }

  test("Parse RH sinusitis instrument number") {
    val instrumentNumber = rhFixture.result.getInstrumentNumber
    assert(instrumentNumber.getNumber === 9)
    assert(instrumentNumber.getYear === 2010)
  }

  test("Parse RH sinusitis citation") {
    assert(rhFixture.result.getCitation === "Statement of Principles concerning " +
      "sinusitis No. 9 of 2010")
  }

  test("Parse RH sinusitis condition name") {
    assert(rhFixture.result.getConditionName === "sinusitis")
  }

  test("Parse RH sinusitis effective from date") {
    assert(rhFixture.result.getEffectiveFromDate === LocalDate.of(2010, 3, 10))
  }

  test("Parse RH sinusitis standard of proof") {
    assert(rhFixture.result.getStandardOfProof === StandardOfProof.ReasonableHypothesis)
  }

  // ICD codes
  test("Parse RH sinusitis ICD codes") {
    val icdCodes = rhFixture.result.getICDCodes
    assert(icdCodes.contains(new BasicICDCode("ICD-10-AM", "J01")))
    assert(icdCodes.contains(new BasicICDCode("ICD-10-AM", "J32")))
    assert(icdCodes.size() === 2)
  }

  // Onset factors
  test("Parse RH sinusitis onset factors") {
    val a = new ParsedFactor("6(a)",
      "having a viral respiratory tract infection at the time of the clinical onset of sinusitis",
      Set(respiratoryDef))

    val b = new ParsedFactor("6(b)",
      "having impaired drainage of the sinus at the time of the clinical onset of sinusitis",
      Set(drainageDef))

    val c = new ParsedFactor("6(c)",
      "being infected with human immunodeficiency virus at the time of the clinical onset of sinusitis",
      Nil.toSet)

    val d = new ParsedFactor("6(d)",
      "being in an immunocompromised state at the time of the clinical onset of sinusitis",
      Set(immunocompromisedDef))

    val e = new ParsedFactor("6(e)",
      "having diabetes mellitus at the time of the clinical onset of sinusitis",
      Nil.toSet)

    val f = new ParsedFactor("6(f)",
      "inhaling a specified substance which results in:\r\n(i) acute nasal symptoms or signs within " +
        "48 hours of the\r\ninhalation; and\r\n(ii) scarring or erosion of the nasal or sinus mucosa,\r\n" +
        "before the clinical onset of sinusitis",
      Set(nasalDef, substanceDef))

    val g = new ParsedFactor("6(g)",
      "for sinusitis affecting the maxillary sinus only, having a specified dental condition " +
        "affecting the tissues adjacent to the affected maxillary sinus at the time of the " +
        "clinical onset of sinusitis",
      Set(dentalDef))

    val h = new ParsedFactor("6(h)",
      "having allergic rhinitis at the time of the clinical onset of sinusitis",
      Nil.toSet)

    val i = new ParsedFactor("6(i)",
      "having sinus barotrauma at the time of the clinical onset of sinusitis",
      Nil.toSet)

    val j = new ParsedFactor("6(j)",
      "undergoing a course of therapeutic radiation to the head within the six " +
        "weeks before the clinical onset of sinusitis",
      Nil.toSet)

    val k = new ParsedFactor("6(k)",
      "smoking on average at least ten cigarettes per day, or the equivalent thereof in " +
        "other tobacco products and having smoked at least one pack year of cigarettes, or " +
        "the equivalent thereof in other tobacco products, at the time of the clinical onset " +
        "of sinusitis",
      Set(cigsPerDayDef, cigsPackYearDef))

    val l = new ParsedFactor("6(l)",
      "having gastroesophageal reflux disease at the time of the clinical onset of sinusitis",
      Nil.toSet)

    val onsetFactors = rhFixture.result.getOnsetFactors
    assert(onsetFactors.contains(a))
    assert(onsetFactors.contains(b))
    assert(onsetFactors.contains(c))
    assert(onsetFactors.contains(d))
    assert(onsetFactors.contains(e))
    assert(onsetFactors.contains(f))
    assert(onsetFactors.contains(g))
    assert(onsetFactors.contains(h))
    assert(onsetFactors.contains(i))
    assert(onsetFactors.contains(j))
    assert(onsetFactors.contains(k))
    assert(onsetFactors.contains(l))
  }

//  // Aggravation factors
//  test("Parse RH sinusitis aggravation factors") {
//val m = new ParsedFactor("6(m)",
//  "flying in a powered aircraft as operational aircrew, for a cumulative total of at " +
//    "least 1 000 hours within the 25 years before the clinical onset of sinusitis",
//  Nil.toSet)
//
//  val n = new ParsedFactor("6(n)",
//    "extreme forward flexion of the lumbar spine for a cumulative total of at least " +
//      "1 500 hours before the clinical onset of sinusitis",
//    Nil.toSet)
//
//  val o = new ParsedFactor("6(o)",
//    "having acromegaly involving the lumbar spine before the clinical onset of sinusitis",
//    Nil.toSet)
//
//  val p = new ParsedFactor("6(p)",
//    "having Paget's disease of bone involving the lumbar spine before the clinical onset of sinusitis",
//    Nil.toSet)

//    val q = new ParsedFactor("6(q)",
//      "having inflammatory joint disease in the lumbar spine before the clinical " +
//        "worsening of sinusitis",
//      Nil.toSet)
//
//    val r = new ParsedFactor("6(r)",
//      "having an infection of the affected joint as specified at least one " +
//        "year before the clinical worsening of sinusitis",
//      Nil.toSet)
//
//    val s = new ParsedFactor("6(s)",
//      "having an intra-articular fracture of the lumbar spine at least one " +
//        "year before the clinical worsening of sinusitis",
//      Nil.toSet)
//
//    val t = new ParsedFactor("6(t)",
//      "having a specified spinal condition affecting the lumbar spine for at " +
//        "least the one year before the clinical worsening of sinusitis",
//      Nil.toSet)
//
//    val u = new ParsedFactor("6(u)",
//      "having leg length inequality for at least the two years before the " +
//        "clinical worsening of sinusitis",
//      Nil.toSet)
//
//    val v = new ParsedFactor("6(v)",
//      "having a depositional joint disease in the lumbar spine before the " +
//        "clinical worsening of sinusitis",
//      Nil.toSet)
//
//    val w = new ParsedFactor("6(w)",
//      "having trauma to the lumbar spine at least one year before the clinical " +
//        "worsening of sinusitis",
//      Nil.toSet)
//
//    val x = new ParsedFactor("6(x)",
//      "having a lumbar intervertebral disc prolapse before the clinical worsening " +
//        "of sinusitis at the level of the intervertebral disc prolapse",
//      Nil.toSet)
//
//    val y = new ParsedFactor("6(y)",
//      "lifting loads of at least 25 kilograms while bearing weight through the " +
//        "lumbar spine to a cumulative total of at least 120 000 kilograms within " +
//        "any ten year period before the clinical worsening of sinusitis",
//      Nil.toSet)
//
//    val z = new ParsedFactor("6(z)",
//      "carrying loads of at least 25 kilograms while bearing weight through the " +
//        "lumbar spine to a cumulative total of at least 3 800 hours within any ten " +
//        "year period before the clinical worsening of sinusitis",
//      Nil.toSet)
//
//    val aa = new ParsedFactor("6(aa)",
//      "being obese for at least ten years before the clinical worsening of sinusitis",
//      Nil.toSet)
//
//    val bb = new ParsedFactor("6(bb)",
//      "flying in a powered aircraft as operational aircrew, for a cumulative total of at " +
//        "least 1 000 hours within the 25 years before the clinical worsening of sinusitis",
//      Nil.toSet)
//
//    val cc = new ParsedFactor("6(cc)",
//      "extreme forward flexion of the lumbar spine for a cumulative total of at least " +
//        "1 500 hours before the clinical worsening of sinusitis",
//      Nil.toSet)
//
//    val dd = new ParsedFactor("6(dd)",
//      "having acromegaly involving the lumbar spine before the clinical worsening of " +
//        "sinusitis",
//      Nil.toSet)
//
//    val ee = new ParsedFactor("6(ee)",
//      "having Paget's disease of bone involving the lumbar spine before the clinical " +
//        "worsening of sinusitis",
//      Nil.toSet)
//
//    val ff = new ParsedFactor("6(ff)",
//      "inability to obtain appropriate clinical management for sinusitis",
//      Nil.toSet)
//
//  assert(rhFixture.result.getOnsetFactors.contains(m))
//  assert(rhFixture.result.getOnsetFactors.contains(n))
//  assert(rhFixture.result.getOnsetFactors.contains(o))
//  assert(rhFixture.result.getOnsetFactors.contains(p))
//    assert(rhFixture.result.getAggravationFactors.contains(q))
//    assert(rhFixture.result.getAggravationFactors.contains(r))
//    assert(rhFixture.result.getAggravationFactors.contains(s))
//    assert(rhFixture.result.getAggravationFactors.contains(t))
//    assert(rhFixture.result.getAggravationFactors.contains(u))
//    assert(rhFixture.result.getAggravationFactors.contains(v))
//    assert(rhFixture.result.getAggravationFactors.contains(w))
//    assert(rhFixture.result.getAggravationFactors.contains(x))
//    assert(rhFixture.result.getAggravationFactors.contains(y))
//    assert(rhFixture.result.getAggravationFactors.contains(z))
//    assert(rhFixture.result.getAggravationFactors.contains(aa))
//    assert(rhFixture.result.getAggravationFactors.contains(bb))
//    assert(rhFixture.result.getAggravationFactors.contains(cc))
//    assert(rhFixture.result.getAggravationFactors.contains(dd))
//    assert(rhFixture.result.getAggravationFactors.contains(ee))
//    assert(rhFixture.result.getAggravationFactors.contains(ff))
//  }
//
//  test("Parse entire BoP LS SoP")
//  {
//    System.out.println(TestUtils.prettyPrint(StoredSop.toJson(bopFixture.result)))
//    assert(bopFixture.result != null)
//  }
//
//  test("Parse BoP sinusitis register ID") {
//    assert(bopFixture.result.getRegisterId === "F2014L00930")
//  }
//
//  test("Parse BoP sinusitis instrument number") {
//    assert(bopFixture.result.getInstrumentNumber.getNumber === 63)
//    assert(bopFixture.result.getInstrumentNumber.getYear === 2014)
//  }
//
//  test("Parse BoP sinusitis citation") {
//    assert(bopFixture.result.getCitation === "Statement of Principles concerning " +
//      "sinusitis No. 63 of 2014")
//  }
//
//  test("Parse BoP sinusitis condition name") {
//    assert(bopFixture.result.getConditionName === "sinusitis")
//  }
//
//  test("Parse BoP sinusitis effective from date") {
//    assert(bopFixture.result.getEffectiveFromDate === LocalDate.of(2014, 7, 2))
//  }
//
//  test("Parse BoP sinusitis standard of proof") {
//    assert(bopFixture.result.getStandardOfProof === StandardOfProof.BalanceOfProbabilities)
//  }
//
//  // ICD codes
//  test("Parse BoP sinusitis ICD codes") {
//    assert(bopFixture.result.getICDCodes.contains(new BasicICDCode("ICD-10-AM", "M47.16")))
//    assert(bopFixture.result.getICDCodes.contains(new BasicICDCode("ICD-10-AM", "M47.17")))
//    assert(bopFixture.result.getICDCodes.contains(new BasicICDCode("ICD-10-AM", "M47.26")))
//    assert(bopFixture.result.getICDCodes.contains(new BasicICDCode("ICD-10-AM", "M47.27")))
//    assert(bopFixture.result.getICDCodes.contains(new BasicICDCode("ICD-10-AM", "M47.86")))
//    assert(bopFixture.result.getICDCodes.contains(new BasicICDCode("ICD-10-AM", "M47.87")))
//    assert(bopFixture.result.getICDCodes.contains(new BasicICDCode("ICD-10-AM", "M47.96")))
//    assert(bopFixture.result.getICDCodes.contains(new BasicICDCode("ICD-10-AM", "M47.97")))
//    assert(bopFixture.result.getICDCodes.contains(new BasicICDCode("ICD-10-AM", "M51.3")))
//  }
//
//  // Onset factors
//  test("Parse BoP sinusitis onset factors") {
//    val a = new ParsedFactor("6(a)",
//      "having inflammatory joint disease in the lumbar spine before the clinical " +
//        "onset of sinusitis",
//      Nil.toSet)
//
//    val b = new ParsedFactor("6(b)",
//      "having an infection of the affected joint as specified at least one year " +
//        "before the clinical onset of sinusitis",
//      Nil.toSet)
//
//    val c = new ParsedFactor("6(c)",
//      "having an intra-articular fracture of the lumbar spine at least one year " +
//        "before the clinical onset of sinusitis",
//      Nil.toSet)
//
//    val d = new ParsedFactor("6(d)",
//      "having a specified spinal condition affecting the lumbar spine for at " +
//        "least the one year before the clinical onset of sinusitis",
//      Nil.toSet)
//
//    val e = new ParsedFactor("6(e)",
//      "having leg length inequality for at least the five years before the clinical " +
//        "onset of sinusitiss",
//      Nil.toSet)
//
//    val f = new ParsedFactor("6(f)",
//      "having a depositional joint disease in the lumbar spine before the clinical " +
//        "onset of sinusitis",
//      Nil.toSet)
//
//    val g = new ParsedFactor("6(g)",
//      "having trauma to the lumbar spine at least one year before the clinical onset " +
//        "of sinusitis, and where the trauma to the lumbar spine occurred " +
//        "within the 25 years before the clinical onset of sinusitis",
//      Nil.toSet)
//
//    val h = new ParsedFactor("6(h)",
//      "having a lumbar intervertebral disc prolapse before the clinical onset of lumbar " +
//        "spondylosis at the level of the intervertebral disc prolapse",
//      Nil.toSet)
//
//    val i = new ParsedFactor("6(i)",
//      "lifting loads of at least 35 kilograms while bearing weight through the lumbar " +
//        "spine to a cumulative total of at least 168 000 kilograms within any ten year " +
//        "period before the clinical onset of sinusitis, and where the clinical " +
//        "onset of sinusitis occurs within the 25 years following that period",
//      Nil.toSet)
//
//    val j = new ParsedFactor("6(j)",
//      "carrying loads of at least 35 kilograms while bearing weight through the lumbar " +
//        "spine to a cumulative total of at least 3 800 hours within any ten year period " +
//        "before the clinical onset of sinusitis, and where the clinical onset " +
//        "of sinusitis occurs within the 25 years following that period",
//      Nil.toSet)
//
//    val k = new ParsedFactor("6(k)",
//      "being obese for at least ten years within the 25 years before the clinical onset " +
//        "of sinusitis",
//      Nil.toSet)
//
//    val l = new ParsedFactor("6(l)",
//      "flying in a powered aircraft as operational aircrew, for a cumulative total of at least 2 000 " +
//        "hours within the 25 years before the clinical onset of sinusitis",
//      Nil.toSet)
//
//    val m = new ParsedFactor("6(m)",
//      "extreme forward flexion of the lumbar spine for a cumulative total of at least 1 500 hours " +
//        "before the clinical onset of sinusitis",
//      Nil.toSet)
//
//    val n = new ParsedFactor("6(n)",
//      "having acromegaly involving the lumbar spine before the clinical onset of sinusitis",
//      Nil.toSet)
//
//    val o = new ParsedFactor("6(o)",
//      "having Paget's disease of bone involving the lumbar spine before the clinical onset of " +
//        "sinusitis",
//      Nil.toSet)
//
//    assert(bopFixture.result.getOnsetFactors.contains(a))
//    assert(bopFixture.result.getOnsetFactors.contains(b))
//    assert(bopFixture.result.getOnsetFactors.contains(c))
//    assert(bopFixture.result.getOnsetFactors.contains(d))
//    assert(bopFixture.result.getOnsetFactors.contains(e))
//    assert(bopFixture.result.getOnsetFactors.contains(f))
//    assert(bopFixture.result.getOnsetFactors.contains(g))
//    assert(bopFixture.result.getOnsetFactors.contains(h))
//    assert(bopFixture.result.getOnsetFactors.contains(i))
//    assert(bopFixture.result.getOnsetFactors.contains(j))
//    assert(bopFixture.result.getOnsetFactors.contains(k))
//    assert(bopFixture.result.getOnsetFactors.contains(l))
//    assert(bopFixture.result.getOnsetFactors.contains(m))
//    assert(bopFixture.result.getOnsetFactors.contains(n))
//    assert(bopFixture.result.getOnsetFactors.contains(o))
//  }
//
//  // Aggravation factors
//  test("Parse BoP sinusitis aggravation factors") {
//    val p = new ParsedFactor("6(p)",
//      "having inflammatory joint disease in the lumbar spine before the clinical " +
//        "worsening of sinusitis",
//      Nil.toSet)
//
//    val q = new ParsedFactor("6(q)",
//      "having an infection of the affected joint as specified at least one year " +
//        "before the clinical worsening of sinusitis",
//      Nil.toSet)
//
//    val r = new ParsedFactor("6(r)",
//      "having an intra-articular fracture of the lumbar spine at least one year before " +
//        "the clinical worsening of sinusitis",
//      Nil.toSet)
//
//    val s = new ParsedFactor("6(s)",
//      "having a specified spinal condition affecting the lumbar spine for at least " +
//        "the one year before the clinical worsening of sinusitis",
//      Nil.toSet)
//
//    val t = new ParsedFactor("6(t)",
//      "having leg length inequality for at least the five years before the clinical " +
//        "worsening of sinusitis",
//      Nil.toSet)
//
//    val u = new ParsedFactor("6(u)",
//      "having a depositional joint disease in the lumbar spine before the clinical " +
//        "worsening of sinusitis",
//      Nil.toSet)
//
//    val v = new ParsedFactor("6(v)",
//      "having trauma to the lumbar spine at least one year before the clinical " +
//        "worsening of sinusitis, and where the trauma to the lumbar spine " +
//        "occurred within the 25 years before the clinical worsening of sinusitis",
//      Nil.toSet)
//
//    val w = new ParsedFactor("6(w)",
//      "having a lumbar intervertebral disc prolapse before the clinical worsening of lumbar " +
//        "spondylosis at the level of the intervertebral disc prolapse",
//      Nil.toSet)
//
//    val x = new ParsedFactor("6(x)",
//      "lifting loads of at least 35 kilograms while bearing weight through the lumbar " +
//        "spine to a cumulative total of at least 168 000 kilograms within any ten year " +
//        "period before the clinical worsening of sinusitis, and where the clinical " +
//        "worsening of sinusitis occurs within the 25 years following that period",
//      Nil.toSet)
//
//    val y = new ParsedFactor("6(y)",
//      "carrying loads of at least 35 kilograms while bearing weight through the lumbar " +
//        "spine to a cumulative total of at least 3 800 hours within any ten year period " +
//        "before the clinical worsening of sinusitis, and where the clinical " +
//        "worsening of sinusitis occurs within the 25 years following that period",
//      Nil.toSet)
//
//    val z = new ParsedFactor("6(z)",
//      "being obese for at least ten years within the 25 years before the clinical " +
//        "worsening of sinusitis",
//      Nil.toSet)
//
//    val aa = new ParsedFactor("6(aa)",
//      "flying in a powered aircraft as operational aircrew, for a cumulative total of at least " +
//        "2 000 hours within the 25 years before the clinical worsening of sinusitis",
//      Nil.toSet)
//
//    val bb = new ParsedFactor("6(bb)",
//      "extreme forward flexion of the lumbar spine for a cumulative total of at least 1 500 " +
//        "hours before the clinical worsening of sinusitis",
//      Nil.toSet)
//
//    val cc = new ParsedFactor("6(cc)",
//      "having acromegaly involving the lumbar spine before the clinical worsening of " +
//        "sinusitis",
//      Nil.toSet)
//
//    val dd = new ParsedFactor("6(dd)",
//      "having Paget's disease of bone involving the lumbar spine before the clinical " +
//        "worsening of sinusitis",
//      Nil.toSet)
//
//    val ee = new ParsedFactor("6(ee)",
//      "inability to obtain appropriate clinical management for sinusitis",
//      Nil.toSet)
//
//    assert(bopFixture.result.getAggravationFactors.contains(q))
//    assert(bopFixture.result.getAggravationFactors.contains(r))
//    assert(bopFixture.result.getAggravationFactors.contains(s))
//    assert(bopFixture.result.getAggravationFactors.contains(t))
//    assert(bopFixture.result.getAggravationFactors.contains(u))
//    assert(bopFixture.result.getAggravationFactors.contains(v))
//    assert(bopFixture.result.getAggravationFactors.contains(w))
//    assert(bopFixture.result.getAggravationFactors.contains(x))
//    assert(bopFixture.result.getAggravationFactors.contains(y))
//    assert(bopFixture.result.getAggravationFactors.contains(z))
//    assert(bopFixture.result.getAggravationFactors.contains(aa))
//    assert(bopFixture.result.getAggravationFactors.contains(bb))
//    assert(bopFixture.result.getAggravationFactors.contains(cc))
//    assert(bopFixture.result.getAggravationFactors.contains(dd))
//    assert(bopFixture.result.getAggravationFactors.contains(ee))
//  }

}
