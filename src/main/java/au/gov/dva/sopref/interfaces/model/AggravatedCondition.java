package au.gov.dva.sopref.interfaces.model;

import java.time.LocalDate;
import java.util.Optional;

public interface AggravatedCondition extends Condition
{
    LocalDate getAggravationStartDate();
    Optional<LocalDate> getAggravationEndDate();
}
