package au.gov.dva.sopapi.sopsupport.processingrules.rules;

import au.gov.dva.sopapi.dtos.Rank;
import au.gov.dva.sopapi.interfaces.AccumulationRule;
import au.gov.dva.sopapi.interfaces.ProcessingRule;
import au.gov.dva.sopapi.interfaces.model.*;
import au.gov.dva.sopapi.sopsupport.processingrules.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.function.Predicate;

public class LumbarSpondylosisRule extends ProcessingRuleBase implements ProcessingRule, AccumulationRule {

    private static RuleSpecification _simpleRuleSpec = null;
    //new SimpleRuleSpec(
      //          new RankSpec(Rank.Unknown, 49,3726,26),
        //        new RankSpec(Rank.Officer, 49,3726,23),
          //      new RankSpec(Rank.OtherRank, 37,3280,26),
            //    new RankSpec(Rank.SpecialForces, 6,21267,4)
           // );

    public LumbarSpondylosisRule() {
    }

    @Override
    public ImmutableSet<String> appliesToInstrumentIds() {
        return ImmutableSet.of("F2014L00933","F2014L00930");
    }

    @Override
    public SoP getApplicableSop(Condition condition, ServiceHistory serviceHistory, Predicate<Deployment> isOperational) {
        return super.getApplicableSop(_simpleRuleSpec,condition,serviceHistory,isOperational);
    }


    @Override
    public ImmutableList<FactorWithSatisfaction> getSatisfiedFactors(Condition condition, SoP applicableSop, ServiceHistory serviceHistory) {
        ImmutableList<Factor> applicableFactors =  condition.getApplicableFactors(applicableSop);

        if (!ProcessingRuleFunctions.conditionStartedWithinXYearsOfLastDayOfMRCAService(condition,serviceHistory,25))
            return ProcessingRuleFunctions.withSatisfiedFactors(applicableFactors);

        return super.getSatisfiedFactors(_simpleRuleSpec,condition,applicableSop,serviceHistory,"6(j)","6(y)");
    }


    @Override
    public Long getAccumulation() {
        return null;
    }

    @Override
    public String getAccumulationUnit() {
        return "kg";
    }
}