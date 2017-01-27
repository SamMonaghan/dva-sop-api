package au.gov.dva.sopapi.sopref.data.updates.types;

import au.gov.dva.sopapi.exceptions.AutoUpdateError;
import au.gov.dva.sopapi.interfaces.JsonSerializable;
import au.gov.dva.sopapi.interfaces.Repository;
import au.gov.dva.sopapi.interfaces.model.InstrumentChange;
import au.gov.dva.sopapi.interfaces.model.InstrumentChangeBase;
import au.gov.dva.sopapi.interfaces.model.SoP;
import au.gov.dva.sopapi.sopref.data.sops.StoredSop;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.function.Function;

// A SoP is repealed and replaced with one with the same name.
// Shows in the repealed by area of Legislation Register.
public class SopReplacement extends InstrumentChangeBase implements InstrumentChange, JsonSerializable {


    public SopReplacement(String newInstrumentRegisterId, OffsetDateTime date, String oldInstrumentRegisterId) {
        super(oldInstrumentRegisterId, newInstrumentRegisterId, date);
    }


    @Override
    public OffsetDateTime getDate() {
        return super.getDate();
    }

    @Override
    public String getSourceInstrumentId() {
        return super.getSourceRegisterId();
    }

    @Override
    public String getTargetInstrumentId() {
        return super.getTargetRegisterId();
    }

    @Override
    public String toString() {
        return "Replacement{} " + super.toString();
    }

    @Override
    public void apply(Repository repository, Function<String, Optional<SoP>> soPProvider) {

        Optional<SoP> newInstrument = repository.getSop(getTargetRegisterId());
        if (newInstrument.isPresent())
            return;

        Optional<SoP> toEndDate = repository.getSop(getSourceInstrumentId());
        if (!toEndDate.isPresent())
        {
            throw new AutoUpdateError(String.format("Attempt to update the end date of SoP %s failed because it is not present in the Repository.", getSourceInstrumentId()));
        }

        Optional<SoP> repealingSop = soPProvider.apply(this.getTargetInstrumentId());
        if (!repealingSop.isPresent())
        {
            throw new AutoUpdateError(String.format("Replacement of repealed SoP %s failed because could not obtain new SoP %s", getSourceInstrumentId(), getTargetInstrumentId()));
        }

        LocalDate effectiveDateOfNewSoP = repealingSop.get().getEffectiveFromDate();
        SoP endDated = StoredSop.withEndDate(toEndDate.get(), effectiveDateOfNewSoP.minusDays(1));
        repository.saveSop(endDated);
        repository.saveSop(repealingSop.get());
    }

    public static final String TYPE_NAME = "replacement";
    private static final String REPEALED_LABEL = "repealedRegisterId";
    private static final String NEW_LABEL = "newRegisterId";

    @Override
    public JsonNode toJson() {
        ObjectNode root = getCommonNode(TYPE_NAME,getDate());
        root.put(REPEALED_LABEL,getSourceInstrumentId());
        root.put(NEW_LABEL,getTargetInstrumentId());
        return root;
    }

    public static SopReplacement fromJson(JsonNode jsonNode)
    {
        return new SopReplacement(jsonNode.findValue(REPEALED_LABEL).asText(), extractDate(jsonNode),jsonNode.findValue(NEW_LABEL).asText());
    }

}
