package au.gov.dva.sopapi.sopref.data.updates.types;

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
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.function.Function;

public class Revocation extends InstrumentChangeBase implements InstrumentChange, JsonSerializable {
    private final LocalDate revocationDate;

    public Revocation(String registerId, OffsetDateTime date, LocalDate revocationDate) {
        super(registerId, date);
        this.revocationDate = revocationDate;
    }

    public static final String TYPE_NAME = "revocation";

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

        Optional<SoP> existing = repository.getSop(getInstrumentId());
        if (!existing.isPresent())
            return;

        repository.archiveSoP(getInstrumentId());
        SoP endDated = StoredSop.withEndDate(existing.get(), revocationDate);
        repository.saveSop(endDated);
    }

    private static final String REVOCATION_DATE = "revocationDate";

    @Override
    public JsonNode toJson() {
        ObjectNode root = getCommonNode(TYPE_NAME,getInstrumentId(),getDate());
        root.put(REVOCATION_DATE, revocationDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        return root;
    }

    public static Revocation fromJson(JsonNode jsonNode)
    {
        String repealDateString =  jsonNode.findValue(REVOCATION_DATE).asText();
        LocalDate repealDate = LocalDate.parse(repealDateString,DateTimeFormatter.ISO_LOCAL_DATE);
        return new Revocation(extractInstrumentId(jsonNode),extractDate(jsonNode), repealDate);
    }
}
