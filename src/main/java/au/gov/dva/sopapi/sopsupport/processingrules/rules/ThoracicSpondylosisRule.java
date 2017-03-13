package au.gov.dva.sopapi.sopsupport.processingrules.rules;

import au.gov.dva.sopapi.interfaces.AccumulationRule;
import au.gov.dva.sopapi.interfaces.ProcessingRule;
import au.gov.dva.sopapi.interfaces.model.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.function.Predicate;

public class ThoracicSpondylosisRule extends ProcessingRuleBase implements ProcessingRule, AccumulationRule {
    @Override
    public Long getAccumulation() {
        return null;
    }

    @Override
    public String getAccumulationUnit() {
        return null;
    }

    @Override
    public ImmutableSet<String> appliesToInstrumentIds() {
        return ImmutableSet.of();
    }

    @Override
    public SoP getApplicableSop(Condition condition, ServiceHistory serviceHistory, Predicate<Deployment> isOperational) {
        return null;
    }

    @Override
    public ImmutableList<FactorWithSatisfaction> getSatisfiedFactors(Condition condition, SoP applicableSop, ServiceHistory serviceHistory) {
        return null;
    }
}
