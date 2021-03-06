package au.gov.dva.sopapi.dtos.sopsupport.components;

import au.gov.dva.sopapi.dtos.sopsupport.OffsetDateTimeDeserializer;
import au.gov.dva.sopapi.dtos.sopsupport.OffsetDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.OffsetDateTime;

public class OperationalServiceDto {

    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonProperty("assigned")
    private final OffsetDateTime _assigned;


    @JsonProperty(value = "description", required = true)
    private final String _description; // eg Operation Warden

    @JsonProperty(value = "event", required = true)
    private final String _event; // eg Within Specified Area

    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonProperty(value = "startDate", required = true)
    private final OffsetDateTime _startDate;

    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonProperty("endDate")
    private final OffsetDateTime _endDate;

    @JsonCreator
    public OperationalServiceDto(

            @JsonProperty("assigned")
            @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
            OffsetDateTime _assigned,

            @JsonProperty("description")
            String _description,

            @JsonProperty("event")
            String _event,

            @JsonProperty("startDate")
            @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
            OffsetDateTime _startDate,


            @JsonProperty("endDate")
            @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
            OffsetDateTime _endDate) {
        this._assigned = _assigned;
        this._description = _description;
        this._event = _event;
        this._startDate = _startDate;
        this._endDate = _endDate;
    }

    public OffsetDateTime get_assigned() {
        return _assigned;
    }

    public String get_description() {
        return _description;
    }

    public String get_event() {
        return _event;
    }

    public OffsetDateTime get_startDate() {
        return _startDate;
    }

    public OffsetDateTime get_endDate() {
        return _endDate;
    }
}
