package au.gov.dva.dvasopapi.tests;

import au.gov.dva.dvasopapi.tests.mocks.MockLumbarSpondylosisSop;
import au.gov.dva.sopref.SoPs;
import au.gov.dva.interfaces.model.IncidentType;
import au.gov.dva.interfaces.model.SoP;
import au.gov.dva.interfaces.model.StandardOfProof;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class SoPReferenceServiceTests {

    @Test
    public void testSerializationOfDto() {

        SoP mockSop = new MockLumbarSpondylosisSop();
        StandardOfProof standardOfProof = StandardOfProof.ReasonableHypothesis;
        IncidentType incidentType = IncidentType.Aggravation;

        String json = SoPs.buildSopRefJsonResponse(ImmutableSet.of(mockSop),incidentType,standardOfProof);
        System.out.print(json);
        Assert.assertTrue(!json.isEmpty());
    }

    @Test
    public void checkHowCompareWorksForLocalDates()
    {
        LocalDate d1 = LocalDate.of(2000,1,1);
        LocalDate d2 = LocalDate.of(2010,1,1);
        LocalDate d3 = LocalDate.of(2010,1,1);

        List<LocalDate> sorted = ImmutableList.of(d1,d2,d3)
                .stream()
                .sorted((o1, o2) -> o2.compareTo(o1))
                .collect(Collectors.toList());

        assert(sorted.get(0).isEqual(d2));

    }



}
