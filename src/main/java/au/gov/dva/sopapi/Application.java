package au.gov.dva.sopapi;

import au.gov.dva.sopapi.exceptions.DvaSopApiError;
import au.gov.dva.sopapi.interfaces.Repository;
import au.gov.dva.sopapi.sopref.data.AzureStorageRepository;
import au.gov.dva.sopapi.sopref.data.FederalRegisterOfLegislationClient;
import au.gov.dva.sopapi.sopref.data.updates.AutoUpdate;
import au.gov.dva.sopapi.sopref.data.updates.LegislationRegisterEmailClientImpl;
import au.gov.dva.sopapi.sopref.data.updates.changefactories.EmailSubscriptionInstrumentChangeFactory;
import au.gov.dva.sopapi.sopref.data.updates.changefactories.LegislationRegisterSiteChangeFactory;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Application implements spark.servlet.SparkApplication {

    private final Repository _repository;
    private final Cache _cache = Cache.getInstance();

    private static Logger logger = LoggerFactory.getLogger(Application.class);

    public Application() {
        _repository = new AzureStorageRepository(au.gov.dva.sopapi.AppSettings.AzureStorage.getConnectionString());
        seedStorage();
        autoUpdate();
    }

    private void seedStorage() {
        if (_repository.getAllSops().isEmpty())
        {

        }
    }

    private void autoUpdate(){

        try {
            updateNow();
            startScheduledUpdates();
        }
        catch (DvaSopApiError e)
        {
            logger.error("Error occurred during update.",e);
        }
    }



    private void startScheduledUpdates() {
        startScheduledPollingForSoPChanges(LocalTime.of(15,30));
        startScheduledLoadingOfSops(LocalTime.of(20,0));
    }

    private void updateNow()
    {
        updateSops().run();
        AutoUpdate.patchChanges(_repository);
        _cache.refresh(_repository);
    }

    @Override
    public void init() {
        Routes.init(_cache);
    }


    private Runnable updateSops() {
        return () ->  AutoUpdate.updateChangeList(
                _repository,
                new EmailSubscriptionInstrumentChangeFactory(
                        new LegislationRegisterEmailClientImpl("noreply@legislation.gov.au"),
                        () -> _repository.getLastUpdated().orElse(OffsetDateTime.now().minusDays(1))),
                new LegislationRegisterSiteChangeFactory(
                        new FederalRegisterOfLegislationClient(),
                        () -> _repository.getAllSops().stream().map(
                                s -> s.getRegisterId())
                                .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableSet::copyOf))));
    }

    private Runnable updateServiceDeterminations() {

        return () -> AutoUpdate.updateServiceDeterminations(_repository, new FederalRegisterOfLegislationClient());
    }

    private void startScheduledPollingForSoPChanges(LocalTime runTime) {
       startDailyExecutor(runTime, updateSops());
    }


    private void startScheduledLoadingOfSops(LocalTime runTime) {
        // idea: provide antecedent sop register ID also
        startDailyExecutor(runTime,() -> {
            AutoUpdate.patchChanges(_repository);
            _cache.refresh(_repository);
        });

    }

    private void startDailyExecutor(LocalTime runTime, Runnable runnable)
    {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        OffsetDateTime nowCanberraTime = OffsetDateTime.now(ZoneId.of(DateTimeUtils.TZDB_REGION_CODE));
        OffsetDateTime threeThirtyAmTodayCanberraTime = OffsetDateTime.from(
                ZonedDateTime.of(nowCanberraTime.toLocalDate(),
                        runTime,
                        ZoneId.of(DateTimeUtils.TZDB_REGION_CODE)));
        OffsetDateTime nextScheduledTime = threeThirtyAmTodayCanberraTime.isAfter(nowCanberraTime) ? threeThirtyAmTodayCanberraTime : threeThirtyAmTodayCanberraTime.plusDays(1);
        long minutesToNextScheduledTime = Duration.between(nowCanberraTime, nextScheduledTime).toMinutes();
        scheduledExecutorService.scheduleAtFixedRate(runnable,
                minutesToNextScheduledTime,
                Duration.ofDays(1).toMinutes(),
                TimeUnit.MINUTES);

    }

}
