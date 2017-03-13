package au.gov.dva.sopapi.sopsupport.ruleconfiguration;

import au.gov.dva.sopapi.ConfigurationError;
import au.gov.dva.sopapi.dtos.Rank;
import au.gov.dva.sopapi.dtos.ServiceBranch;
import au.gov.dva.sopapi.exceptions.SopParserError;
import au.gov.dva.sopapi.interfaces.BoPRuleConfigurationItem;
import au.gov.dva.sopapi.interfaces.RuleConfigurationItem;
import com.google.common.collect.ImmutableSet;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ParsedRuleConfigurationItem implements RuleConfigurationItem {

    private String conditionName;
    private String instrumentId;
    private ImmutableSet<String> factorRefs;
    private final ServiceBranch serviceBranch;
    private final Rank rank;
    private final int cftsWeeks;
    private final int accumRate;
    private final String accumUnit;

    public ParsedRuleConfigurationItem(String conditionName, String instrumentId, String factorRefs, String serviceBranch, String rank, String cftsWeeks, String accumRate, String accumUnit)
    {
        this.conditionName = conditionName.trim().toLowerCase();
        this.instrumentId = instrumentId.trim();
        this.factorRefs = splitFactorRefs(factorRefs);
        this.rank = toRank(rank);
        this.serviceBranch = toServiceBranch(serviceBranch);
        this.cftsWeeks = toInt(cftsWeeks,"Cannot determine number of CFTS weeks from");
        this.accumRate = toInt(accumRate,"Cannot determine the accumulation rate from");
        this.accumUnit = accumUnit.trim().toLowerCase();
    }

    private ImmutableSet<String> splitFactorRefs(String factorRefsCellValue){

        Stream<String> refs =  Arrays.stream(factorRefsCellValue.split("[,;]")).map(String::trim);
        refs.forEach(r ->  {
            if (!r.matches("[0-9\\(\\)a-z]+"))
            {
                throw new ConfigurationError(String.format("Illegal factor reference in cell: %s", factorRefsCellValue));
            }
        });

        return refs.collect(Collectors.collectingAndThen(Collectors.toList(),ImmutableSet::copyOf));
    }

    private Rank toRank(String rank)
    {
        try {
            Rank parsed = Rank.fromString(rank.trim());
            return parsed;
        }
        catch (Exception e)
        {
            throw new ConfigurationError(e);
        }
    }

    private ServiceBranch toServiceBranch(String serviceBranch)
    {
        try {
            ServiceBranch parsed = ServiceBranch.fromString(serviceBranch.trim());
            return parsed;
        }
        catch (Exception e)
        {
            throw new ConfigurationError(e);
        }
    }

    protected int toInt(String intString, String errMsg)
    {
        try {
            return Integer.parseInt(intString.trim());
        }
        catch (Exception e)
        {
            throw new ConfigurationError(errMsg + ": " + intString);
        }
    }

    @Override
    public String getConditionName() {
        return null;
    }

    @Override
    public String getInstrumentId() {
        return null;
    }

    @Override
    public ImmutableSet<String> getFactorReferences() {
        return null;
    }

    @Override
    public ServiceBranch getServiceBranch() {
        return null;
    }

    @Override
    public Rank getRank() {
        return null;
    }

    @Override
    public int getRequiredCFTSWeeks() {
        return 0;
    }

    @Override
    public int getAccumulationRatePerWeek() {
        return 0;
    }

    @Override
    public String getAccumulationUnit() {
        return null;
    }
}

