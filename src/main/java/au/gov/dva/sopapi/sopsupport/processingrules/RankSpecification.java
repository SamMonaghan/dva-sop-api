package au.gov.dva.sopapi.sopsupport.processingrules;

import au.gov.dva.sopapi.dtos.Rank;

public interface RankSpecification {
    Integer getRequiredWeeksCfts();

    Integer getAccumPerWeek();

    Integer getRhDaysOfOpServiceInLast10Years();

    Rank getRank();
}
