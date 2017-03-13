package au.gov.dva.sopapi.interfaces;

import au.gov.dva.sopapi.dtos.Rank;
import au.gov.dva.sopapi.dtos.ServiceBranch;
import au.gov.dva.sopapi.sopsupport.processingrules.RuleSpecification;
import com.google.common.collect.ImmutableSet;

public interface RuleConfigurationItem {
    String getConditionName();

    String getInstrumentId();

    ImmutableSet<String> getFactorReferences();

    ServiceBranch getServiceBranch();

    Rank getRank();

    int getRequiredCFTSWeeks();

    int getAccumulationRatePerWeek();

    String getAccumulationUnit();
}



