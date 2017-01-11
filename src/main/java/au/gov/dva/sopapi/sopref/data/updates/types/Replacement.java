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

import javax.mail.Store;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.function.Function;

// A SoP is repealed and replaced with one with the same name.
// Shows in the repealed by area of Legislation Register.
public class Replacement extends InstrumentChangeBase implements InstrumentChange, JsonSerializable {
    private final String oldInstrumentRegisterId;

    public Replacement(String newInstrumentRegisterId, OffsetDateTime date, String oldInstrumentRegisterId) {
        super(newInstrumentRegisterId, date);
        this.oldInstrumentRegisterId = oldInstrumentRegisterId;
    }

    @Override
    public String getInstrumentId() {
        return super.getInstrumentId();
    }

    @Override
    public OffsetDateTime getDate() {
        return super.getDate();
    }

    @Override
    public void apply(Repository repository, Function<String, Optional<SoP>> soPProvider) {

        Optional<SoP> newInstrument = repository.getSop(getInstrumentId());
        if (newInstrument.isPresent())
            return;

        Optional<SoP> toEndDate = repository.getSop(oldInstrumentRegisterId);
        if (!toEndDate.isPresent())
        {
            throw new AutoUpdateError(String.format("Attempt to update the end date of SoP %s failed because it is not present in the Repository.", oldInstrumentRegisterId));
        }

        Optional<SoP> repealingSop = soPProvider.apply(this.getInstrumentId());
        if (!repealingSop.isPresent())
        {
            throw new AutoUpdateError(String.format("Replacement of repealed SoP %s failed because could not obtain new SoP %s", oldInstrumentRegisterId, getInstrumentId()));
        }

        LocalDate effectiveDateOfNewSoP = repealingSop.get().getEffectiveFromDate();
        SoP endDated = StoredSop.withEndDate(toEndDate.get(), effectiveDateOfNewSoP.minusDays(1));
        repository.saveSop(endDated);
        repository.saveSop(repealingSop.get());
    }

    @Override
    public String toString() {
        return "Replacement{" +
                "oldInstrumentRegisterId='" + oldInstrumentRegisterId + '\'' +
                "} " + super.toString();
    }

    public static final String TYPE_NAME = "replacement";
    private static final String REPLACED_LABEL = "replaced";

    @Override
    public JsonNode toJson() {
        ObjectNode root = getCommonNode(TYPE_NAME,getInstrumentId(),getDate());
        root.put(REPLACED_LABEL,oldInstrumentRegisterId);
        return root;
    }

    public static Replacement fromJson(JsonNode jsonNode)
    {
        return new Replacement(extractInstrumentId(jsonNode),extractDate(jsonNode),
                jsonNode.findValue(REPLACED_LABEL).asText());
    }

}
