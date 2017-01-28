package au.gov.dva.sopapi;

import au.gov.dva.sopapi.exceptions.InitialSeedingError;
import au.gov.dva.sopapi.interfaces.Repository;
import au.gov.dva.sopapi.interfaces.model.SopChange;
import au.gov.dva.sopapi.sopref.data.updates.types.NewSop;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

class Seeds {

    public static void addSops(Repository repository) {
        try {
            String[] registerIdsOfInitialSops =  Resources.toString(Resources.getResource("initialsops.txt"), Charsets.UTF_8).split("\\r?\\n");
            ImmutableSet<SopChange> newInstruments = Arrays.stream(registerIdsOfInitialSops).map(id -> new NewSop(id, OffsetDateTime.now()))
                    .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableSet::copyOf));
            repository.addInstrumentChanges(newInstruments);
        } catch (IOException e) {
            throw new InitialSeedingError(e);
        }

    }

    public static void addServiceDeterminations(Repository repository)
    {
        try {
            String[] registerIdsOfInitialServiceDeterminations =  Resources.toString(Resources.getResource("initialservicedeterminations.txt"), Charsets.UTF_8).split("\\r?\\n");
            ImmutableSet<SopChange> newInstruments = Arrays.stream(registerIdsOfInitialServiceDeterminations).map(id -> new NewSop(id, OffsetDateTime.now()))
                    .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableSet::copyOf));

            // todo

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
