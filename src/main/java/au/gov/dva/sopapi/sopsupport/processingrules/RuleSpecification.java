package au.gov.dva.sopapi.sopsupport.processingrules;

import au.gov.dva.sopapi.dtos.Rank;

import java.util.Optional;

public interface RuleSpecification {
    Optional<RankSpecification> getSpec(Rank rank);
    RankSpecification getSpecOrDefault(Rank rank);
}
