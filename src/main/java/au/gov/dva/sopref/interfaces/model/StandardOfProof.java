package au.gov.dva.sopref.interfaces.model;

public enum StandardOfProof {

    ReasonableHypothesis("Reasonable Hypothesis"),
    BalanceOfProbabilities("Balance of Probabilities");

    // valueOf doesn't work with white space
    public static StandardOfProof fromString(String text){
        if (text.contentEquals(StandardOfProof.ReasonableHypothesis.toString()))
            return StandardOfProof.ReasonableHypothesis;
        if (text.contentEquals(StandardOfProof.BalanceOfProbabilities.toString()))
            return StandardOfProof.BalanceOfProbabilities;
        throw new IllegalArgumentException(String.format("Cannot convert this text to Standard of Proof: %s", text));
    }

    public static StandardOfProof fromAbbreviation(String abbreviatedText)
    {
        if (abbreviatedText.contentEquals("RH"))
            return StandardOfProof.ReasonableHypothesis;
        if (abbreviatedText.contentEquals("BoP"))
            return StandardOfProof.BalanceOfProbabilities;
        throw new IllegalArgumentException(String.format("Cannot convert this text to Standard of Proof: %s", abbreviatedText));
    }


    @Override
    public String toString() {
        return text;
    }

    private String text;
    StandardOfProof(String text)
    {
        this.text = text;
    }

}
