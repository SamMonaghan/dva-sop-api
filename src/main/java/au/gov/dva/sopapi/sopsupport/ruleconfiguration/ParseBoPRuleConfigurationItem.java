package au.gov.dva.sopapi.sopsupport.ruleconfiguration;

import au.gov.dva.sopapi.interfaces.BoPRuleConfigurationItem;

public final class ParseBoPRuleConfigurationItem extends ParsedRuleConfigurationItem implements BoPRuleConfigurationItem {
    public ParseBoPRuleConfigurationItem(String conditionName, String instrumentId, String factorRefs, String serviceBranch, String rank, String cftsWeeks, String accumRate, String accumUnit) {

        super(conditionName, instrumentId, factorRefs, serviceBranch, rank, cftsWeeks, accumRate, accumUnit);
    }


}
