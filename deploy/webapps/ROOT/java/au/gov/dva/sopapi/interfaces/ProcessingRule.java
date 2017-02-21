package au.gov.dva.sopapi.interfaces;

import au.gov.dva.sopapi.interfaces.model.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.function.Predicate;

public interface ProcessingRule {

    ImmutableSet<String> appliesToInstrumentIds();
    SoP getApplicableSop(Condition condition, ServiceHistory serviceHistory, Predicate<Deployment> isOperational);
    ImmutableList<FactorWithSatisfaction> getSatisfiedFactors(Condition condition, SoP applicableSop, ServiceHistory serviceHistory);
}

