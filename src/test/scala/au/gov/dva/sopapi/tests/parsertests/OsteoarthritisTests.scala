
package au.gov.dva.sopapi.tests.parsertests

import java.time.LocalDate

import au.gov.dva.dvasopapi.tests.TestUtils
import au.gov.dva.sopapi.dtos.StandardOfProof
import au.gov.dva.sopapi.sopref.data.sops.{BasicICDCode, StoredSop}
import au.gov.dva.sopapi.sopref.parsing.implementations.extractors.PreAugust2015Extractor
import au.gov.dva.sopapi.sopref.parsing.implementations.model.{ParsedDefinedTerm, ParsedFactor}
import au.gov.dva.sopapi.sopref.parsing.implementations.parsers.{OsteoarthritisParser}
import au.gov.dva.sopapi.tests.parsers.ParserTestUtils
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class OsteoarthritisTests extends FunSuite {

  val rhFixture = new {
    val result = ParserTestUtils.executeWholeParsingPipeline("F2011C00491", "sops_rh/F2011C00491.pdf")
  }

  val bopFixture = new {
    val result = ParserTestUtils.executeWholeParsingPipeline("F2011C00492", "sops_bop/F2011C00492.pdf")
  }
  
  val depositionalDef = new ParsedDefinedTerm("a depositional joint disease",
    "means gout, calcium pyrophosphate\ndihydrate deposition disease (also known as pseudogout),\n" +
      "haemochromatosis, Wilson’s disease or alkaptonuria (also known as\nochronosis)")

  val lowerLimbDef = new ParsedDefinedTerm("a joint of the lower limb",
    "means the hip, knee, ankle, sacro-iliac joint\nor any joint of the foot")

  val acromegalyDef = new ParsedDefinedTerm("acromegaly",
    "means a chronic disease of adults resulting from\nhypersecretion of growth hormone " +
      "after closure of the epiphyses")

  val infectionDef = new ParsedDefinedTerm("an infection of the affected joint as specified",
    "means the bacterial\ninfection of a joint resulting in inflammation within that joint " +
      "or\ninfection of a joint by a virus, fungus or parasite resulting in\ninflammation " +
      "and destruction of articular cartilage within that joint")

  val intraArticularDef = new ParsedDefinedTerm("an intra-articular fracture",
    "means a fracture involving the articular\nsurface of a joint")

  val overweightDef = new ParsedDefinedTerm("being overweight",
    "means an increase in body weight by way of fat accumulation which results in a " +
      "Body Mass Index (BMI) of 25 or greater. The BMI = W/H^2 and where: W is the person's " +
      "weight in kilograms and  H is the person's height in metres")

  val disorderedDef = new ParsedDefinedTerm("disordered joint mechanics",
    "means maldistribution of loading forces\non that joint resulting from:\n" +
      "(a) a rotation or angulation deformity of the bones of the affected\nlimb; or\n" +
      "(b) a rotation or angulation deformity of the joint of the affected limb")

  val forcefulDef = new ParsedDefinedTerm("forceful activities",
    "means:\n(a) tasks requiring the generation of force by the hand equivalent to\n" +
      "lifting or carrying loads of more than three kilograms; or\n(b) holding or " +
      "carrying an object in the hand greater than one\nkilogram in excess of 10 times per hour")

  val haemarthrosisDef = new ParsedDefinedTerm("haemarthrosis",
    "means bleeding into the joint")

  val handJointDef = new ParsedDefinedTerm("hand joint",
    "means the interphalangeal, metacarpophalangeal, carpometacarpal and intercarpal joints of the " +
      "hand.  This definition excludes\nthe wrist joint")

  val boneMineralDef = new ParsedDefinedTerm("increased bone mineral density",
    "means a bone mineral density at\nleast one standard deviation above the mean bone mineral " +
      "density of\nyoung adult sex-matched controls")

  val inflammatoryDef = new ParsedDefinedTerm("inflammatory joint disease",
    "means rheumatoid arthritis, reactive\narthritis, psoriatic arthropathy, ankylosing " +
      "spondylitis, or arthritis\nassociated with Crohn’s disease or ulcerative colitis")

  val liftingLoadsDef = new ParsedDefinedTerm("lifting loads",
    "means manually raising an object")

  val repetitiveDef = new ParsedDefinedTerm("repetitive activities",
    "means:\n(a) bending or twisting of the affected joint; or\n(b) carrying out the same " +
      "or similar movements that involve the\naffected joint,\nat least 50 times per hour")

  val traumaDef = new ParsedDefinedTerm("trauma to the affected joint",
    "means a discrete event involving the\napplication of significant physical force to " +
      "or through the affected joint,\nthat causes damage to the joint and the development, " +
      "within 24 hours of\nthe event occurring, of symptoms and signs of pain, and " +
      "tenderness, and\neither altered mobility or range of movement of the joint. " +
      "These\nsymptoms and signs must last for a period of at least seven days\n" +
      "following their onset; save for where medical intervention for the trauma\nto " +
      "that joint has occurred and that medical intervention involves either:\n" +
      "(a) immobilisation of the joint or limb by splinting, or similar\nexternal agent; or\n" +
      "(b) injection of corticosteroids or local anaesthetics into that joint; or\n" +
      "(c) surgery to that joint")

  test("Parse entire RH osteoarthritis SoP") {
    System.out.print(TestUtils.prettyPrint(StoredSop.toJson(rhFixture.result)))
    assert(rhFixture.result != null)
  }

  test("Parse RH osteoarthritis register ID") {
    assert(rhFixture.result.getRegisterId === "F2011C00491")
  }

  test("Parse RH osteoarthritis instrument number") {
    val instrumentNumber = rhFixture.result.getInstrumentNumber
    assert(instrumentNumber.getNumber === 13)
    assert(instrumentNumber.getYear === 2010)
  }

  test("Parse RH osteoarthritis citation") {
    assert(rhFixture.result.getCitation === "Statement of Principles concerning " +
      "osteoarthritis No. 13 of 2010")
  }

  test("Parse RH osteoarthritis condition name") {
    assert(rhFixture.result.getConditionName === "osteoarthritis")
  }

  test("Parse RH osteoarthritis effective from date") {
    assert(rhFixture.result.getEffectiveFromDate === LocalDate.of(2010, 3, 10))
  }

  test("Parse RH osteoarthritis standard of proof") {
    assert(rhFixture.result.getStandardOfProof === StandardOfProof.ReasonableHypothesis)
  }

  // ICD codes
  test("Parse RH osteoarthritis ICD codes") {
    val icdCodes = rhFixture.result.getICDCodes
    assert(icdCodes.size() === 5)
    assert(icdCodes.contains(new BasicICDCode("ICD-10-AM", "M15")))
    assert(icdCodes.contains(new BasicICDCode("ICD-10-AM", "M16")))
    assert(icdCodes.contains(new BasicICDCode("ICD-10-AM", "M17")))
    assert(icdCodes.contains(new BasicICDCode("ICD-10-AM", "M18")))
    assert(icdCodes.contains(new BasicICDCode("ICD-10-AM", "M19")))
  }

  // Onset factors
  test("Parse RH osteoarthritis onset factors") {
    val a = new ParsedFactor("6(a)",
      "being a prisoner of war before the clinical onset of osteoarthritis",
      Nil.toSet)

    val b = new ParsedFactor("6(b)",
      "having inflammatory joint disease of the affected joint before the clinical onset " +
        "of osteoarthritis in that joint",
      Set(inflammatoryDef))

    val c = new ParsedFactor("6(c)",
      "having an infection of the affected joint as specified before the clinical onset of " +
        "osteoarthritis in that joint",
      Set(infectionDef))

    val d = new ParsedFactor("6(d)",
      "having an intra-articular fracture of the affected joint before the clinical onset " +
        "of osteoarthritis in that joint",
      Set(intraArticularDef))

    val e = new ParsedFactor("6(e)",
      "having haemarthrosis of the affected joint before the clinical onset of osteoarthritis " +
        "in that joint",
      Set(haemarthrosisDef))

    val f = new ParsedFactor("6(f)",
      "having a depositional joint disease in the affected joint before the clinical onset " +
        "of osteoarthritis in that joint",
      Set(depositionalDef))

    val g = new ParsedFactor("6(g)",
      "having trauma to the affected joint before the clinical onset of osteoarthritis in " +
        "that joint",
      Set(traumaDef))

    val h = new ParsedFactor("6(h)",
      "having frostbite involving the affected joint before the clinical onset of osteoarthritis " +
        "in that joint",
      Nil.toSet)

    val i = new ParsedFactor("6(i)",
      "having disordered joint mechanics of the affected joint for at least three years before " +
        "the clinical onset of osteoarthritis in that joint",
      Set(disorderedDef))

    val j = new ParsedFactor("6(j)",
      "having necrosis of the subchondral bone near the affected joint before the clinical " +
        "onset of osteoarthritis in that joint",
      Nil.toSet)

    val k = new ParsedFactor("6(k)",
      "for osteoarthritis of a joint of the upper limb only, (i) performing any combination " +
        "of repetitive activities or forceful activities for an average of at least 30 hours " +
        "per week, for a continuous period of at least ten years before the clinical onset " +
        "of osteoarthritis in that joint; or (i) using a hand-held, vibrating, percussive, " +
        "industrial tool on more days than not, for at least 10 years before the clinical " +
        "onset of osteoarthritis in that joint",
      Set(forcefulDef, repetitiveDef))

    val l = new ParsedFactor("6(l)",
      "for osteoarthritis of a joint of the lower limb only, (i) having an amputation " +
        "involving either leg; or (ii) having an asymmetric gait; for at least three years " +
        "before the clinical onset of osteoarthritis in that joint",
      Set(lowerLimbDef))

    val m = new ParsedFactor("6(m)",
      "for osteoarthritis of a joint of the lower limb only, (i) lifting loads of at least " +
        "25 kilograms while bearing weight through the affected joint to a cumulative total " +
        "of at least 120 000 kilograms within any 10 year period before the clinical onset " +
        "of osteoarthritis in that joint; or (ii) carrying loads of at least 25 kilograms " +
        "while bearing weight through the affected joint to a cumulative total of at least " +
        "3800 hours within any ten year period before the clinical onset of osteoarthritis " +
        "in that joint; or (iii) having increased bone mineral density before the clinical " +
        "onset of osteoarthritis in that joint",
      Set(lowerLimbDef, boneMineralDef, liftingLoadsDef))

    val n = new ParsedFactor("6(n)",
      "for osteoarthritis of a joint of the lower limb or hand joint only, (i) being overweight " +
        "for at least 10 years before the clinical onset of osteoarthritis in that joint; or " +
        "(ii) for males, having a waist to hip circumference ratio exceeding 1.0 for at least " +
        "10 years, before the clinical onset of osteoarthritis in that joint; or (iii) for " +
        "females, having a waist to hip circumference ratio exceeding 0.9 for at least 10 years, " +
        "before the clinical onset of osteoarthritis in that joint",
      Set(lowerLimbDef, overweightDef, handJointDef))

    val o = new ParsedFactor("6(o)",
      "for osteoarthritis of a hip or knee joint only, ascending or descending at least 300 " +
        "stairs or rungs of a ladder per day, on more days than not, for a continuous period " +
        "of at least two years, before the clinical onset of osteoarthritis in that joint",
      Nil.toSet)

    val p = new ParsedFactor("6(p)",
      "for osteoarthritis of a knee joint only, (i) kneeling or squatting for a cumulative " +
        "period of at least one hour per day, on more days than not, for a continuous period " +
        "of at least one year before the clinical onset of osteoarthritis in that joint; or " +
        "(ii) having internal derangement of the knee before the clinical onset of " +
        "osteoarthritis in that joint",
      Nil.toSet)

    val q = new ParsedFactor("6(q)",
      "for osteoarthritis of the patello-femoral joint only, having chondromalacia patellae " +
        "before the clinical onset of osteoarthritis in that joint",
      Nil.toSet)

    val r = new ParsedFactor("6(r)",
      "having a disorder associated with loss of pain sensation or proprioception involving " +
        "the affected joint before the clinical onset of osteoarthritis in that joint",
      Nil.toSet)

    val s = new ParsedFactor("6(s)",
      "having Paget’s disease of bone of the affected joint before the clinical onset of " +
        "osteoarthritis in that joint",
      Nil.toSet)

    val t = new ParsedFactor("6(t)",
      "having acromegaly before the clinical onset of osteoarthritis in that joint",
      Set(acromegalyDef))

    val onsetFactors = rhFixture.result.getOnsetFactors
    assert(onsetFactors.size() === 20)
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
    assert(onsetFactors.contains(m))
    assert(onsetFactors.contains(n))
    assert(onsetFactors.contains(o))
    assert(onsetFactors.contains(p))
    assert(onsetFactors.contains(q))
    assert(onsetFactors.contains(r))
    assert(onsetFactors.contains(s))
    assert(onsetFactors.contains(t))
  }

  // Aggravation factors
  test("Parse RH osteoarthritis aggravation factors") {
    val u = new ParsedFactor("6(u)",
      "having inflammatory joint disease of the affected joint before the clinical worsening " +
        "of osteoarthritis in that joint",
      Set(inflammatoryDef))

    val v = new ParsedFactor("6(v)",
      "having an infection of the affected joint as specified before the clinical worsening " +
        "of osteoarthritis in that joint",
      Set(infectionDef))

    val w = new ParsedFactor("6(w)",
      "having an intra-articular fracture of the affected joint before the clinical " +
        "worsening of osteoarthritis in that joint",
      Set(intraArticularDef))

    val x = new ParsedFactor("6(x)",
      "having haemarthrosis of the affected joint before the clinical worsening of " +
        "osteoarthritis in that joint",
      Set(haemarthrosisDef))

    val y = new ParsedFactor("6(y)",
      "having a depositional joint disease in the affected joint before the clinical " +
        "worsening of osteoarthritis in that joint",
      Set(depositionalDef))

    val z = new ParsedFactor("6(z)",
      "having trauma to the affected joint before the clinical worsening of " +
        "osteoarthritis in that joint",
      Set(traumaDef))

    val aa = new ParsedFactor("6(aa)",
      "having frostbite involving the affected joint before the clinical worsening of " +
        "osteoarthritis in that joint",
      Nil.toSet)

    val bb = new ParsedFactor("6(bb)",
      "having disordered joint mechanics of the affected joint for at least three years " +
        "before the clinical worsening of osteoarthritis in that joint",
      Set(disorderedDef))

    val cc = new ParsedFactor("6(cc)",
      "having necrosis of the subchondral bone near the affected joint before the clinical " +
        "worsening of osteoarthritis in that joint",
      Nil.toSet)

    val dd = new ParsedFactor("6(dd)",
      "for osteoarthritis of a joint of the upper limb only, (i) performing any combination of " +
        "repetitive activities or forceful activities for an average of at least 30 hours per " +
        "week, for a continuous period of at least ten years before the clinical worsening of " +
        "osteoarthritis in that joint; or (ii) using a hand-held, vibrating, percussive, " +
        "industrial tool on more days than not, for at least 10 years before the clinical " +
        "worsening of osteoarthritis in that joint",
      Set(forcefulDef, repetitiveDef))

    val ee = new ParsedFactor("6(ee)",
      "for osteoarthritis of a joint of the lower limb only, (i) having an amputation involving " +
        "either leg; or (ii) having an asymmetric gait; for at least three years before the " +
        "clinical worsening of osteoarthritis in that joint",
      Set(lowerLimbDef))

    val ff = new ParsedFactor("6(ff)",
      "for osteoarthritis of a joint of the lower limb only, (i) lifting loads of at least 25 " +
        "kilograms while bearing weight through the affected joint to a cumulative total of at " +
        "least 120 000 kilograms within any 10 year period before the clinical worsening of " +
        "osteoarthritis in that joint; or (ii) carrying loads of at least 25 kilograms while " +
        "bearing weight through the affected joint to a cumulative total of at least 3800 hours " +
        "within any ten year period before the clinical worsening of osteoarthritis in that joint",
      Set(lowerLimbDef, liftingLoadsDef))

    val gg = new ParsedFactor("6(gg)",
      "for osteoarthritis of a joint of the lower limb or hand joint only, (i) being overweight " +
        "for at least 10 years before the clinical worsening of osteoarthritis in that joint; or " +
        "(ii) for males, having a waist to hip circumference ratio exceeding 1.0 for at least " +
        "10 years, before the clinical worsening of osteoarthritis in that joint; or (iii) " +
        "for females, having a waist to hip circumference ratio exceeding 0.9 for at least " +
        "10 years, before the clinical worsening of osteoarthritis in that joint",
      Set(lowerLimbDef, overweightDef, handJointDef))

    val hh = new ParsedFactor("6(hh)",
      "for osteoarthritis of a hip or knee joint only, ascending or descending at least 300 " +
        "stairs or rungs of a ladder per day, on more days than not, for a continuous period " +
        "of at least two years, before the clinical worsening of osteoarthritis in that joint",
      Nil.toSet)

    val ii = new ParsedFactor("6(ii)",
      "for osteoarthritis of a knee joint only, (i) kneeling or squatting for a cumulative " +
        "period of at least one hour per day, on more days than not, for a continuous period " +
        "of at least one year before the clinical worsening of osteoarthritis in that joint; or " +
        "(ii) having internal derangement of the knee before the clinical worsening of " +
        "osteoarthritis in that joint",
      Nil.toSet)

    val jj = new ParsedFactor("6(jj)",
      "for osteoarthritis of the patello-femoral joint only, having chondromalacia patellae " +
        "before the clinical worsening of osteoarthritis in that joint",
      Nil.toSet)

    val kk = new ParsedFactor("6(kk)",
      "having a disorder associated with loss of pain sensation or proprioception involving " +
        "the affected joint before the clinical worsening of osteoarthritis in that joint",
      Nil.toSet)

    val ll = new ParsedFactor("6(ll)",
      "having Paget’s disease of bone of the affected joint before the clinical worsening " +
        "of osteoarthritis in that joint",
      Nil.toSet)

    val mm = new ParsedFactor("6(mm)",
      "having acromegaly before the clinical worsening of osteoarthritis in that joint",
      Set(acromegalyDef))

    val nn = new ParsedFactor("6(nn)",
      "inability to obtain appropriate clinical management for osteoarthritis",
      Nil.toSet)

    val aggravationFactors = rhFixture.result.getAggravationFactors
    assert(aggravationFactors.size() === 20)
    assert(aggravationFactors.contains(u))
    assert(aggravationFactors.contains(v))
    assert(aggravationFactors.contains(w))
    assert(aggravationFactors.contains(x))
    assert(aggravationFactors.contains(y))
    assert(aggravationFactors.contains(z))
    assert(aggravationFactors.contains(aa))
    assert(aggravationFactors.contains(bb))
    assert(aggravationFactors.contains(cc))
    assert(aggravationFactors.contains(dd))
    assert(aggravationFactors.contains(ee))
    assert(aggravationFactors.contains(ff))
    assert(aggravationFactors.contains(gg))
    assert(aggravationFactors.contains(hh))
    assert(aggravationFactors.contains(ii))
    assert(aggravationFactors.contains(jj))
    assert(aggravationFactors.contains(kk))
    assert(aggravationFactors.contains(ll))
    assert(aggravationFactors.contains(mm))
    assert(aggravationFactors.contains(nn))
  }

  test("Parse entire BoP osteoarthritis SoP")
  {
    System.out.println(TestUtils.prettyPrint(StoredSop.toJson(bopFixture.result)))
    assert(bopFixture.result != null)
  }

  test("Parse BoP osteoarthritis register ID") {
    assert(bopFixture.result.getRegisterId === "F2011C00492")
  }

  test("Parse BoP osteoarthritis instrument number") {
    val instrumentNumber = bopFixture.result.getInstrumentNumber
    assert(instrumentNumber.getNumber === 14)
    assert(instrumentNumber.getYear === 2010)
  }

  test("Parse BoP osteoarthritis citation") {
    assert(bopFixture.result.getCitation === "Statement of Principles concerning " +
      "osteoarthritis No. 14 of 2010")
  }

  test("Parse BoP osteoarthritis condition name") {
    assert(bopFixture.result.getConditionName === "osteoarthritis")
  }

  test("Parse BoP osteoarthritis effective from date") {
    assert(bopFixture.result.getEffectiveFromDate === LocalDate.of(2010, 3, 10))
  }

  test("Parse BoP osteoarthritis standard of proof") {
    assert(bopFixture.result.getStandardOfProof === StandardOfProof.BalanceOfProbabilities)
  }

  // ICD codes
  test("Parse BoP osteoarthritis ICD codes") {
    val icdCodes = bopFixture.result.getICDCodes
    assert(icdCodes.size() === 5)
    assert(icdCodes.contains(new BasicICDCode("ICD-10-AM", "M15")))
    assert(icdCodes.contains(new BasicICDCode("ICD-10-AM", "M16")))
    assert(icdCodes.contains(new BasicICDCode("ICD-10-AM", "M17")))
    assert(icdCodes.contains(new BasicICDCode("ICD-10-AM", "M18")))
    assert(icdCodes.contains(new BasicICDCode("ICD-10-AM", "M19")))
  }

  // Onset factors
  test("Parse BoP osteoarthritis onset factors") {
    val a = new ParsedFactor("6(a)",
      "having inflammatory joint disease of the affected joint before the clinical onset " +
        "of osteoarthritis in that joint",
      Set(inflammatoryDef))

    val b = new ParsedFactor("6(b)",
      "having an infection of the affected joint as specified before the clinical onset " +
        "of osteoarthritis in that joint",
      Set(infectionDef))

    val c = new ParsedFactor("6(c)",
      "having an intra-articular fracture of the affected joint before the clinical onset " +
        "of osteoarthritis in that joint",
      Set(intraArticularDef))

    val d = new ParsedFactor("6(d)",
      "having haemarthrosis of the affected joint before the clinical onset of osteoarthritis " +
        "in that joint",
      Set(haemarthrosisDef))

    val e = new ParsedFactor("6(e)",
      "having a depositional joint disease in the affected joint before the clinical onset " +
        "of osteoarthritis in that joint",
      Set(depositionalDef))

    val f = new ParsedFactor("6(f)",
      "having trauma to the affected joint within the 25 years before the clinical onset of " +
        "osteoarthritis in that joint",
      Set(traumaDef))

    val g = new ParsedFactor("6(g)",
      "having frostbite involving the affected joint before the clinical onset of " +
        "osteoarthritis in that joint",
      Nil.toSet)

    val h = new ParsedFactor("6(h)",
      "having disordered joint mechanics of the affected joint for at least five years before " +
        "the clinical onset of osteoarthritis in that joint",
      Set(disorderedDef))

    val i = new ParsedFactor("6(i)",
      "having necrosis of the subchondral bone near the affected joint before the clinical " +
        "onset of osteoarthritis in that joint",
      Nil.toSet)

    val j = new ParsedFactor("6(j)",
      "for osteoarthritis of a joint of the upper limb only, performing any combination " +
        "of repetitive activities or forceful activities for an average of at least 30 " +
        "hours per week, for a continuous period of at least 20 years before the clinical " +
        "onset of osteoarthritis in that joint",
      Set(forcefulDef, repetitiveDef))

    val k = new ParsedFactor("6(k)",
      "for osteoarthritis of a joint of the hand, wrist or elbow joint only, using a " +
        "hand-held, vibrating, percussive, industrial tool on more days than not, for " +
        "at least 10 years within the 25 years before the clinical onset of osteoarthritis " +
        "in that joint",
      Nil.toSet)

    val l = new ParsedFactor("6(l)",
      "for osteoarthritis of a joint of the lower limb only, (i) having an amputation " +
        "involving either leg; or (ii) having an asymmetric gait; for at least five years " +
        "before the clinical onset of osteoarthritis in that joint",
      Set(lowerLimbDef))

    val m = new ParsedFactor("6(m)",
      "for osteoarthritis of a joint of the lower limb only, (i) lifting loads of at least " +
        "35 kilograms while bearing weight through the affected joint to a cumulative total " +
        "of at least 168 000 kilograms within any 10 year period before the clinical onset " +
        "of osteoarthritis in that joint, and where the clinical onset of osteoarthritis in " +
        "that joint occurs within the 25 years following that period; or (ii) carrying loads " +
        "of at least 35 kilograms while bearing weight through the affected joint to a " +
        "cumulative total of at least 3800 hours within any ten year period before the " +
        "clinical onset of osteoarthritis in that joint, and where the clinical onset of " +
        "osteoarthritis in that joint occurs within the 25 years following that period; or " +
        "(iii) having increased bone mineral density before the clinical onset of " +
        "osteoarthritis in that joint",
      Set(lowerLimbDef, boneMineralDef, liftingLoadsDef))

    val n = new ParsedFactor("6(n)",
      "for osteoarthritis of a joint of the lower limb or hand joint only, (i) being " +
        "overweight for at least 10 years before the clinical onset of osteoarthritis " +
        "in that joint; or (ii) for males, having a waist to hip circumference ratio " +
        "exceeding 1.0 for at least 10 years, before the clinical onset of osteoarthritis " +
        "in that joint; or (iii) for females, having a waist to hip circumference ratio " +
        "exceeding 0.9 for at least 10 years, before the clinical onset of osteoarthritis " +
        "in that joint",
      Set(lowerLimbDef, overweightDef, handJointDef))

    val o = new ParsedFactor("6(o)",
      "for osteoarthritis of a hip or knee joint only, ascending or descending at least 300 " +
        "stairs or rungs of a ladder per day, on more days than not, for a continuous period " +
        "of at least 10 years, within the 25 years before the clinical onset of osteoarthritis " +
        "in that joint",
      Nil.toSet)

    val p = new ParsedFactor("6(p)",
      "for osteoarthritis of a knee joint only, (i) kneeling or squatting for a cumulative " +
        "period of at least one hour per day, on more days than not, for a continuous period " +
        "of at least two years before the clinical onset of osteoarthritis in that joint, and " +
        "where the clinical onset of osteoarthritis in that joint occurs within the 25 years " +
        "following that period; or (ii) having internal derangement of the knee before the " +
        "clinical onset of osteoarthritis in that joint",
      Nil.toSet)

    val q = new ParsedFactor("6(q)",
      "for osteoarthritis of the patello-femoral joint only, having chondromalacia patellae " +
        "before the clinical onset of osteoarthritis in that joint",
      Nil.toSet)

    val r = new ParsedFactor("6(r)",
      "having a disorder associated with loss of pain sensation or proprioception involving " +
        "the affected joint before the clinical onset of osteoarthritis in that joint",
      Nil.toSet)

    val s = new ParsedFactor("6(s)",
      "having Paget’s disease of bone of the affected joint before the clinical onset of " +
        "osteoarthritis in that joint",
      Nil.toSet)

    val t = new ParsedFactor("6(t)",
      "having acromegaly before the clinical onset of osteoarthritis in that joint",
      Set(acromegalyDef))

    val onsetFactors = bopFixture.result.getOnsetFactors
    assert(onsetFactors.size() === 20)
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
    assert(onsetFactors.contains(m))
    assert(onsetFactors.contains(n))
    assert(onsetFactors.contains(o))
    assert(onsetFactors.contains(p))
    assert(onsetFactors.contains(q))
    assert(onsetFactors.contains(r))
    assert(onsetFactors.contains(s))
    assert(onsetFactors.contains(t))
  }

  // Aggravation factors
  test("Parse BoP osteoarthritis aggravation factors") {
    val u = new ParsedFactor("6(u)",
      "having inflammatory joint disease of the affected joint before the clinical worsening " +
        "of osteoarthritis in that joint",
      Set(inflammatoryDef))

    val v = new ParsedFactor("6(v)",
      "having an infection of the affected joint as specified before the clinical worsening " +
        "of osteoarthritis in that joint",
      Set(infectionDef))

    val w = new ParsedFactor("6(w)",
      "having an intra-articular fracture of the affected joint before the clinical " +
        "worsening of osteoarthritis in that joint",
      Set(intraArticularDef))

    val x = new ParsedFactor("6(x)",
      "having haemarthrosis of the affected joint before the clinical worsening of " +
        "osteoarthritis in that joint",
      Set(haemarthrosisDef))

    val y = new ParsedFactor("6(y)",
      "having a depositional joint disease in the affected joint before the clinical " +
        "worsening of osteoarthritis in that joint",
      Set(depositionalDef))

    val z = new ParsedFactor("6(z)",
      "having trauma to the affected joint within the 25 years before the clinical " +
        "worsening of osteoarthritis in that joint",
      Set(traumaDef))

    val aa = new ParsedFactor("6(aa)",
      "having frostbite involving the affected joint before the clinical worsening of " +
        "osteoarthritis in that joint",
      Nil.toSet)

    val bb = new ParsedFactor("6(bb)",
      "having disordered joint mechanics of the affected joint for at least five years " +
        "before the clinical worsening of osteoarthritis in that joint",
      Set(disorderedDef))

    val cc = new ParsedFactor("6(cc)",
      "having necrosis of the subchondral bone near the affected joint before the clinical " +
        "worsening of osteoarthritis in that joint",
      Nil.toSet)

    val dd = new ParsedFactor("6(dd)",
      "for osteoarthritis of a joint of the upper limb only, performing any combination of " +
        "repetitive activities or forceful activities for an average of at least 30 hours " +
        "per week, for a continuous period of at least 20 years before the clinical worsening " +
        "of osteoarthritis in that joint",
      Set(forcefulDef, repetitiveDef))

    val ee = new ParsedFactor("6(ee)",
      "for osteoarthritis of a joint of the hand, wrist or elbow joint only, using a hand-held, " +
        "vibrating, percussive, industrial tool on more days than not, for at least 10 years " +
        "within the 25 years before the clinical worsening of osteoarthritis in that joint",
      Nil.toSet)

    val ff = new ParsedFactor("6(ff)",
      "for osteoarthritis of a joint of the lower limb only, (i) having an amputation involving " +
        "either leg; or (ii) having an asymmetric gait; for at least five years before the " +
        "clinical worsening of osteoarthritis in that joint",
      Set(lowerLimbDef))

    val gg = new ParsedFactor("6(gg)",
      "for osteoarthritis of a joint of the lower limb only, (i) lifting loads of at least 35 " +
        "kilograms while bearing weight through the affected joint to a cumulative total of " +
        "at least 168 000 kilograms within any 10 year period before the clinical worsening " +
        "of osteoarthritis in that joint, and where the clinical worsening of osteoarthritis " +
        "in that joint occurs within the 25 years following that period; or (ii) carrying " +
        "loads of at least 35 kilograms while bearing weight through the affected joint to " +
        "a cumulative total of at least 3800 hours within any ten year period before the " +
        "clinical worsening of osteoarthritis in that joint, and where the clinical worsening " +
        "of osteoarthritis in that joint occurs within the 25 years following that period",
      Set(lowerLimbDef, liftingLoadsDef))

    val hh = new ParsedFactor("6(hh)",
      "for osteoarthritis of a joint of the lower limb or hand joint only, (i) being overweight " +
        "for at least 10 years before the clinical worsening of osteoarthritis in that joint; or " +
        "(ii) for males, having a waist to hip circumference ratio exceeding 1.0 for at least " +
        "10 years, before the clinical worsening of osteoarthritis in that joint; or " +
        "(iii) for females, having a waist to hip circumference ratio exceeding 0.9 for at least " +
        "10 years, before the clinical worsening of osteoarthritis in that joint",
      Set(lowerLimbDef, overweightDef, handJointDef))

    val ii = new ParsedFactor("6(ii)",
      "for osteoarthritis of a hip or knee joint only, ascending or descending at least " +
        "300 stairs or rungs of a ladder per day, on more days than not, for a continuous " +
        "period of at least 10 years, within the 25 years before the clinical worsening of " +
        "osteoarthritis in that joint",
      Nil.toSet)

    val jj = new ParsedFactor("6(jj)",
      "for osteoarthritis of a knee joint only, (i) kneeling or squatting for a cumulative " +
        "period of at least one hour per day, on more days than not, for a continuous period " +
        "of at least two years before the clinical worsening of osteoarthritis in that joint, " +
        "and where the clinical worsening of osteoarthritis in that joint occurs within the " +
        "25 years following that period; or (ii) having internal derangement of the knee " +
        "before the clinical worsening of osteoarthritis in that joint",
      Nil.toSet)

    val kk = new ParsedFactor("6(kk)",
      "for osteoarthritis of the patello-femoral joint only, having chondromalacia patellae " +
        "before the clinical worsening of osteoarthritis in that joint",
      Nil.toSet)

    val ll = new ParsedFactor("6(ll)",
      "having a disorder associated with loss of pain sensation or proprioception involving " +
        "the affected joint before the clinical worsening of osteoarthritis in that joint",
      Nil.toSet)

    val mm = new ParsedFactor("6(mm)",
      "having Paget’s disease of bone of the affected joint before the clinical worsening " +
        "of osteoarthritis in that joint",
      Nil.toSet)

    val nn = new ParsedFactor("6(nn)",
      "having acromegaly before the clinical worsening of osteoarthritis in that joint",
      Set(acromegalyDef))

    val oo = new ParsedFactor("6(oo)",
      "inability to obtain appropriate clinical management for osteoarthritis",
      Nil.toSet)

    val aggravationFactors = bopFixture.result.getAggravationFactors
    assert(aggravationFactors.size() === 21)
    assert(aggravationFactors.contains(u))
    assert(aggravationFactors.contains(v))
    assert(aggravationFactors.contains(w))
    assert(aggravationFactors.contains(x))
    assert(aggravationFactors.contains(y))
    assert(aggravationFactors.contains(z))
    assert(aggravationFactors.contains(aa))
    assert(aggravationFactors.contains(bb))
    assert(aggravationFactors.contains(cc))
    assert(aggravationFactors.contains(dd))
    assert(aggravationFactors.contains(ee))
    assert(aggravationFactors.contains(ff))
    assert(aggravationFactors.contains(gg))
    assert(aggravationFactors.contains(hh))
    assert(aggravationFactors.contains(ii))
    assert(aggravationFactors.contains(jj))
    assert(aggravationFactors.contains(kk))
    assert(aggravationFactors.contains(ll))
    assert(aggravationFactors.contains(mm))
    assert(aggravationFactors.contains(nn))
    assert(aggravationFactors.contains(oo))
  }

  test("Cleanse osteoarthritis text for RH") {
    val result = ParserTestUtils.produceCleansedText("F2011C00491", "sops_rh/F2011C00491.pdf")
    println(result)
    assert(result != null)
  }

  test("Extract factors section from RH cleansed text") {
    val result = PreAugust2015Extractor.extractFactorSection(ParserTestUtils.resourceToString("osteoarthritisCleansedText.txt"))
    println(result)
    assert(result != null)
  }

  test("Para with sub paras parses and no tail") {
    val input = "(l) for osteoarthritis of a joint of the lower limb only, (i) having an amputation involving either leg; or (ii) having an asymmetric gait"

    val result = OsteoarthritisParser.parseAll(OsteoarthritisParser.completeFactorWithSubParas, input)

    assert(result.successful)
    println(result)
  }

  test("Para with sub paras and tail") {
    val input = "(l) for osteoarthritis of a joint of the lower limb only, (i) having an amputation involving either leg; or (ii) having an asymmetric gait; for at least three years before the clinical onset of osteoarthritis in that joint"

    val result = OsteoarthritisParser.parseAll(OsteoarthritisParser.completeFactorWithSubParas, input)

    assert(result.successful)
    println(result)
  }

  test("Para with sub paras and tail to object") {
    val input = "(l) for osteoarthritis of a joint of the lower limb only, (i) having an amputation involving either leg; or (ii) having an asymmetric gait; for at least three years before the clinical onset of osteoarthritis in that joint"

    val result = OsteoarthritisParser.parseAll(OsteoarthritisParser.factor, input)

    assert(result.successful)
    println(result)
  }

  test("Para n")
  {
    val input = "(n) for osteoarthritis of a joint of the lower limb or hand joint only, (i) being overweight for at least 10 years before the clinical onset of osteoarthritis in that joint; or (ii) for males, having a waist to hip circumference ratio exceeding 1.0 for at least 10 years, before the clinical onset of osteoarthritis in that joint; or (iii) for females, having a waist to hip circumference ratio exceeding 0.9 for at least 10 years, before the clinical onset of osteoarthritis in that joint"

    val result = OsteoarthritisParser.parseAll(OsteoarthritisParser.twoLevelPara,input)

    println(result)
    assert(result.successful)
  }

  test("Ordinary single para factor works with parser that can also parse two level factors") {
    val input = "(a) being a prisoner of war before the clinical onset of osteoarthritis"
    val result = OsteoarthritisParser.parseAll(OsteoarthritisParser.factor,input)
    println(result)
    assert(result.successful)
  }

  test("Another two level factor")
  {
    val input = "(k) for osteoarthritis of a joint of the upper limb only, (i) performing any combination of repetitive activities or forceful activities for an average of at least 30 hours per week, for a continuous period of at least ten years before the clinical onset of osteoarthritis in that joint; or (i) using a hand-held, vibrating, percussive, industrial tool on more days than not, for at least 10 years before the clinical onset of osteoarthritis in that joint"

    val result = OsteoarthritisParser.parseAll(OsteoarthritisParser.twoLevelPara,input)
    println(result)
    assert(result.successful)
  }

  test("Parse partial factors section")
  {
    val input = "The factor that must as a minimum exist before it can be said that a reasonable hypothesis has been raised connecting osteoarthritis or death from osteoarthritis with the circumstances of a person’s relevant service is: (a) being a prisoner of war before the clinical onset of osteoarthritis; or (b) having inflammatory joint disease of the affected joint before the clinical onset of osteoarthritis in that joint; or (c) having an infection of the affected joint as specified before the clinical onset of osteoarthritis in that joint; or (d) having an intra-articular fracture of the affected joint before the clinical onset of osteoarthritis in that joint; or (e) having haemarthrosis of the affected joint before the clinical onset of osteoarthritis in that joint; or (f) having a depositional joint disease in the affected joint before the clinical onset of osteoarthritis in that joint; or (g) having trauma to the affected joint before the clinical onset of osteoarthritis in that joint; or (h) having frostbite involving the affected joint before the clinical onset of osteoarthritis in that joint; or (i) having disordered joint mechanics of the affected joint for at least three years before the clinical onset of osteoarthritis in that joint; or (j) having necrosis of the subchondral bone near the affected joint before the clinical onset of osteoarthritis in that joint; or  (j) having necrosis of the subchondral bone near the affected joint before the clinical onset of osteoarthritis in that joint; or (k) for osteoarthritis of a joint of the upper limb only, (i) performing any combination of repetitive activities or forceful activities for an average of at least 30 hours per week, for a continuous period of at least ten years before the clinical onset of osteoarthritis in that joint; or (i) using a hand-held, vibrating, percussive, industrial tool on more days than not, for at least 10 years before the clinical onset of osteoarthritis in that joint; or (l) for osteoarthritis of a joint of the lower limb only, (i) having an amputation involving either leg; or (ii) having an asymmetric gait; for at least three years before the clinical onset of osteoarthritis in that joint."

    val result = OsteoarthritisParser.parseAll(OsteoarthritisParser.factorsSection,input)
    println(result)
    assert(result.successful)
  }

  test("Parse entire factors section")
  {
    val input = ParserTestUtils.resourceToString("osteoarthritis factors text.txt")
    val result = OsteoarthritisParser.parseAll(OsteoarthritisParser.factorsSection,input)
    println(result)
    assert(result.successful)
  }

}
