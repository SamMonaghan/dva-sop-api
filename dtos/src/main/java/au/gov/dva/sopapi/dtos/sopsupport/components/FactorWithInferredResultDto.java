package au.gov.dva.sopapi.dtos.sopsupport.components;

import au.gov.dva.sopapi.dtos.sopref.DefinedTermDto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class FactorWithInferredResultDto {


    @JsonProperty("paragraph")
    private final String _paragraph;

    @JsonProperty("text")
    private final String _text;

    @JsonProperty("definedTerms")
    private final List<DefinedTermDto> _definedTerms;
    private final Boolean _satisfied;



    @JsonCreator
    public FactorWithInferredResultDto(@JsonProperty("paragraph") String paragraph, @JsonProperty("text") String text, List<DefinedTermDto> definedTermDtos, Boolean satisfied) {
        _paragraph = paragraph;
        _text = text;
        _definedTerms = definedTermDtos;
        _satisfied = satisfied;
    }

    public String getParagraph() {
        return _paragraph;
    }

    public String getText() {
        return _text;
    }

    public Boolean getSatisfaction() {
        return _satisfied;
    }

    public ImmutableList<DefinedTermDto> getDefinedTerms() {
        return ImmutableList.copyOf(_definedTerms);
    }
}