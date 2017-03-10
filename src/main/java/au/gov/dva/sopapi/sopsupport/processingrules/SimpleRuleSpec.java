package au.gov.dva.sopapi.sopsupport.processingrules;

import au.gov.dva.sopapi.dtos.Rank;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

public class SimpleRuleSpec implements RuleSpecification {

    private RankSpecification defaultSpecification;
    private final ImmutableSet<RankSpecification> rankSpecs;

    public SimpleRuleSpec(@Nonnull RankSpecification defaultSpecification, RankSpecification... rankSpecifications)
    {
        this.defaultSpecification = defaultSpecification;
        rankSpecs = ImmutableSet.copyOf(Arrays.stream(rankSpecifications).collect(Collectors.toList()));
    }

    @Override
    public Optional<RankSpecification> getSpec(Rank rank)
    {
        return rankSpecs.stream()
                .filter(rankSpecification -> rankSpecification.getRank() == rank)
                .findFirst();
    }

    @Override
    public RankSpecification getSpecOrDefault(Rank rank) {
        Optional<RankSpecification> setSpec = getSpec(rank);
        if (!setSpec.isPresent()) {
            return defaultSpecification;
        }
        return setSpec.get();
    }


}




