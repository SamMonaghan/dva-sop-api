package au.gov.dva.sopapi.sopsupport.processingrules.rules;

import au.gov.dva.sopapi.dtos.Rank;
import au.gov.dva.sopapi.interfaces.model.*;
import au.gov.dva.sopapi.sopsupport.processingrules.ProcessingRuleFunctions;
import au.gov.dva.sopapi.sopsupport.processingrules.RankSpecification;
import au.gov.dva.sopapi.sopsupport.processingrules.RuleSpecification;
import com.google.common.collect.ImmutableList;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.function.Predicate;

public class ProcessingRuleBase {

    protected SoP getApplicableSop(RuleSpecification ruleSpecification, Condition condition, ServiceHistory serviceHistory, Predicate<Deployment> isOperational) {

        OffsetDateTime startDateForPeriodOfOperationalService = condition.getStartDate().minusYears(10);
        OffsetDateTime endDateForPeriodOfOperationalService = condition.getStartDate();
        long daysOfOperationalService = ProcessingRuleFunctions.getNumberOfDaysOfOperationalServiceInInterval(
                startDateForPeriodOfOperationalService,endDateForPeriodOfOperationalService,
                ProcessingRuleFunctions.getDeployments(serviceHistory),
                isOperational);

        Rank rank = ProcessingRuleFunctions.getRankProximateToDate(serviceHistory.getServices(),condition.getStartDate());

        RankSpecification specificationForThisRank = null; // = ruleSpecification.getSpecOrDefault(rank);

        Integer minimumRequiredDaysOfOperationalServiceForRank =  specificationForThisRank.getRhDaysOfOpServiceInLast10Years();

        if (minimumRequiredDaysOfOperationalServiceForRank.longValue() <= daysOfOperationalService)
        {
            return condition.getSopPair().getRhSop();
        }
        else return condition.getSopPair().getBopSop();
    }

    public ImmutableList<FactorWithSatisfaction> getSatisfiedFactors(RuleSpecification ruleSpecification, Condition condition, SoP applicableSop, ServiceHistory serviceHistory, String... satisfiedfactorParagraphs) {
        ImmutableList<Factor> applicableFactors =  condition.getApplicableFactors(applicableSop);

        Optional<Service> serviceDuringWhichConditionStarts =  ProcessingRuleFunctions.identifyServiceDuringOrAfterWhichConditionOccurs(serviceHistory.getServices(),condition.getStartDate());

        Rank relevantRank = serviceDuringWhichConditionStarts.get().getRank();

        RankSpecification specificationForThisRank = null; //  = ruleSpecification.getSpecOrDefault(relevantRank);

        Integer cftsDaysRequired = specificationForThisRank.getRequiredWeeksCfts() * 7;

        Long actualDaysOfCfts = ProcessingRuleFunctions.getDaysOfContinuousFullTimeServiceToDate(serviceHistory,condition.getStartDate());

        if (actualDaysOfCfts >= cftsDaysRequired) {
            ImmutableList<FactorWithSatisfaction> inferredFactors =
                    ProcessingRuleFunctions.withSatisfiedFactors(applicableFactors, satisfiedfactorParagraphs);

            return inferredFactors;
        }
        else
        {
            return ProcessingRuleFunctions.withSatisfiedFactors(applicableFactors);
        }
    }
}
