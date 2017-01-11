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

public class RepealWithoutReplacement extends InstrumentChangeBase implements InstrumentChange, JsonSerializable {
    private final LocalDate repealDate;

    public RepealWithoutReplacement(String registerId, OffsetDateTime date, LocalDate repealDate) {
        super(registerId, date);
        this.repealDate = repealDate;
    }

    public static final String TYPE_NAME = "repealwithoutreplacement";

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
        SoP endDated = StoredSop.withEndDate(existing.get(),repealDate);
        repository.saveSop(endDated);
    }

    private static final String REPEAL_DATE_LABEL = "repealDate";

    @Override
    public JsonNode toJson() {
        ObjectNode root = getCommonNode(TYPE_NAME,getInstrumentId(),getDate());
        root.put(REPEAL_DATE_LABEL,repealDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        return root;
    }

    public static RepealWithoutReplacement fromJson(JsonNode jsonNode)
    {
        String repealDateString =  jsonNode.findValue(REPEAL_DATE_LABEL).asText();
        LocalDate repealDate = LocalDate.parse(repealDateString,DateTimeFormatter.ISO_LOCAL_DATE);
        return new RepealWithoutReplacement(extractInstrumentId(jsonNode),extractDate(jsonNode), repealDate);
    }
}
