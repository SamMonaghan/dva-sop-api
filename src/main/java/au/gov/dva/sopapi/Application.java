package au.gov.dva.sopapi;

import au.gov.dva.sopapi.SharedConstants.Routes;
import au.gov.dva.sopapi.dtos.DvaSopApiDtoError;
import au.gov.dva.sopapi.dtos.IncidentType;
import au.gov.dva.sopapi.dtos.QueryParamLabels;
import au.gov.dva.sopapi.dtos.StandardOfProof;
import au.gov.dva.sopapi.dtos.sopref.OperationsResponseDto;
import au.gov.dva.sopapi.dtos.sopsupport.SopSupportRequestDto;
import au.gov.dva.sopapi.dtos.sopsupport.SopSupportResponseDto;
import au.gov.dva.sopapi.interfaces.Repository;
import au.gov.dva.sopapi.interfaces.model.Deployment;
import au.gov.dva.sopapi.interfaces.model.ServiceDetermination;
import au.gov.dva.sopapi.interfaces.model.SoP;
import au.gov.dva.sopapi.interfaces.model.SoPPair;
import au.gov.dva.sopapi.sopref.DtoTransformations;
import au.gov.dva.sopapi.sopref.Operations;
import au.gov.dva.sopapi.sopref.SoPs;
import au.gov.dva.sopapi.sopref.data.AzureStorageRepository;
import au.gov.dva.sopapi.sopref.data.FederalRegisterOfLegislationClient;
import au.gov.dva.sopapi.sopref.data.servicedeterminations.ServiceDeterminationPair;
import au.gov.dva.sopapi.sopref.data.sops.BasicICDCode;
import au.gov.dva.sopapi.sopref.data.updates.AutoUpdate;
import au.gov.dva.sopapi.sopref.data.updates.LegislationRegisterEmailClientImpl;
import au.gov.dva.sopapi.sopref.data.updates.changefactories.EmailSubscriptionInstrumentChangeFactory;
import au.gov.dva.sopapi.sopref.data.updates.changefactories.LegislationRegisterSiteChangeFactory;
import au.gov.dva.sopapi.sopsupport.SopSupport;
import au.gov.dva.sopapi.sopsupport.processingrules.ProcessingRuleFunctions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static spark.Spark.get;

public class Application implements spark.servlet.SparkApplication {

    private final Repository _repository;
    private ImmutableSet<SoP> _allSops;
    private ImmutableSet<SoPPair> _sopPairs;
    private ImmutableSet<ServiceDetermination> _allServiceDeterminations;
    private Predicate<Deployment> _isOperational;

    private static Logger logger = LoggerFactory.getLogger(Application.class);

    public Application() {
        _repository = new AzureStorageRepository(au.gov.dva.sopapi.AppSettings.AzureStorage.getConnectionString());
        _allSops = _repository.getAllSops();
        _sopPairs = SoPs.groupSopsToPairs(_allSops);
        _allServiceDeterminations = _repository.getServiceDeterminations();
        ServiceDeterminationPair latestServiceDeterminations = Operations.getLatestDeterminationPair(_allServiceDeterminations);
        _isOperational = ProcessingRuleFunctions.getIsOperationalPredicate(latestServiceDeterminations);
    }

    @Override
    public void init() {

        startScheduledPollingForSoPChanges(LocalTime.of(15,30));
        

        get("/hello", (req, res) -> {
            return "Hello";
        })
        ;

        get(Routes.GET_OPERATIONS, (req, res) -> {

            if (validateHeaders() && !responseTypeAcceptable(req)) {
                setResponseHeaders(res, false, 406);
                return buildAcceptableContentTypesError();
            }

            ServiceDeterminationPair latestServiceDeterminationPair = Operations.getLatestDeterminationPair(_allServiceDeterminations);

            OperationsResponseDto operationsResponseDto = DtoTransformations.buildOperationsResponseDto(latestServiceDeterminationPair);

            setResponseHeaders(res, true, 200);
            String json = OperationsResponseDto.toJsonString(operationsResponseDto);
            return json;
        });

        get(Routes.GET_SOPFACTORS, (req, res) -> {

            if (validateHeaders() && !responseTypeAcceptable(req)) {
                setResponseHeaders(res, false, 406);
                return buildAcceptableContentTypesError();
            }

            QueryParamsMap queryParamsMap = req.queryMap();
            String icdCodeValue = queryParamsMap.get("icdCodeValue").value();
            String icdCodeVersion = queryParamsMap.get("icdCodeVersion").value();
            String standardOfProof = queryParamsMap.get("standardOfProof").value();
            String conditionName = queryParamsMap.get("conditionName").value();
            String incidentType = queryParamsMap.get("incidentType").value();

            List<String> errors = getSopParamsValidationErrors(icdCodeValue, icdCodeVersion, standardOfProof, conditionName, incidentType);

            if (errors.size() > 0) {
                setResponseHeaders(res, false, 400);
                return "Your request is malformed: \r\n\r\n" + String.join("\r\n", errors);
            }

            ImmutableSet<SoP> matchingSops = SoPs.getMatchingSops(conditionName, new BasicICDCode(icdCodeVersion, icdCodeValue), _allSops);

            if (matchingSops.isEmpty()) {
                setResponseHeaders(res, false, 404);
                return buildErrorMessageShowingRecognisedIcdCodesAndConditionNames(_allSops);
            } else {

                setResponseHeaders(res, true, 200);

                IncidentType it = IncidentType.fromString(incidentType);
                StandardOfProof sp = StandardOfProof.fromAbbreviation(standardOfProof);

                String response = SoPs.buildSopRefJsonResponse(matchingSops, it, sp);
                return response;
            }
        });

        get(Routes.GET_SERVICE_CONNECTION, ((req, res) -> {
            if (validateHeaders() && !responseTypeAcceptable(req)) {
                setResponseHeaders(res, false, 406);
                return buildAcceptableContentTypesError();
            }

            try {
                SopSupportRequestDto sopSupportRequestDto = SopSupportRequestDto.fromJsonString(req.body());

                SopSupportResponseDto sopSupportResponseDto = SopSupport.applyRules(sopSupportRequestDto, _sopPairs, _isOperational);
                setResponseHeaders(res, true, 200);
                return SopSupportResponseDto.toJsonString(sopSupportResponseDto);
            } catch (DvaSopApiDtoError e) {
                setResponseHeaders(res, false, 400);
                Optional<String> schema = generateSchemaForSopSupportRequestDto();
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("%s\n", e.getMessage()));
                if (schema.isPresent()) {
                    sb.append("Request body does not conform to expected schema:\n");
                    sb.append(schema);
                }
                return sb.toString();
            }
        }));
    }

    private static List<String> getSopParamsValidationErrors(String icdCodeValue, String icdCodeVersion, String standardOfProof, String conditionname, String incidentType) {
        List<String> errors = new ArrayList<>();

        if (conditionname == null) {
            String missingICDCodeError = "Need ICD code (query parameter '" + QueryParamLabels.ICD_CODE_VALUE + "') and ICD code version (query paramater '" + QueryParamLabels.ICD_CODE_VERSION + "') if condition name (query parameter '" + QueryParamLabels.CONDITION_NAME + "') is not provided.";
            if (icdCodeValue == null)
                errors.add(buildQueryParamErrorMessage(QueryParamLabels.ICD_CODE_VALUE, missingICDCodeError));

            if (icdCodeVersion == null) {
                errors.add(buildQueryParamErrorMessage(QueryParamLabels.ICD_CODE_VERSION, missingICDCodeError));
            }
        }

        if (standardOfProof == null)
            errors.add(buildQueryParamErrorMessage(QueryParamLabels.STANDARD_OF_PROOF, "required, missing."));

        else {
            if (!standardOfProof.contentEquals("RH") && !standardOfProof.contentEquals("BoP"))
                errors.add(buildQueryParamErrorMessage(QueryParamLabels.STANDARD_OF_PROOF, "acceptable values are 'RH' (for Reasonable Hypothesis) and 'BoP' (for Balance of Probabilities)."));
        }

        if (incidentType == null)
            errors.add(buildQueryParamErrorMessage(QueryParamLabels.INCIDENT_TYPE, "required, missing."));
        else {
            if (!incidentType.contentEquals("aggravation") && !incidentType.contentEquals("onset"))
                errors.add(buildQueryParamErrorMessage(QueryParamLabels.INCIDENT_TYPE, "acceptable values are 'aggravation' and 'onset'."));
        }

        return errors;
    }


    private static String buildQueryParamErrorMessage(String queryParamName, String msg) {
        return String.format("* Query paramater '%s': %s", queryParamName, msg);
    }

    private static String buildErrorMessageShowingRecognisedIcdCodesAndConditionNames(ImmutableSet<SoP> sops) {
        String recognisedConditionNames = String.join("\r\n", sops.stream().map(soP -> "* " + soP.getConditionName()).sorted().distinct().collect(toList()));

        String recognisedICDCodes = String.join("\r\n", sops.stream().flatMap(soP -> soP.getICDCodes().stream())
                .map(code -> String.format("* %s %s", code.getVersion(), code.getCode()))
                .distinct()
                .collect(toList()));

        StringBuilder sb = new StringBuilder();
        sb.append("The condition name and ICD code (if any) you provided did not match any in the database.\r\n\r\n");
        sb.append("Known condition names:\r\n");
        sb.append("======================\r\n");
        sb.append(recognisedConditionNames);
        sb.append("\r\n\r\n");
        sb.append("Known ICD codes:\r\n");
        sb.append("================\r\n");
        sb.append(recognisedICDCodes);

        return sb.toString();

    }


    private Runnable detectSoPChanges() {
        return () -> AutoUpdate.updateChangeList(
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

    private void startScheduledPollingForSoPChanges(LocalTime runTime) {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        // Email updates tend to come in between 2:45 and 3am.
        // Hence we schedule at 3:30am to get tha latest updates as soon as possible.
        OffsetDateTime nowCanberraTime = OffsetDateTime.now(ZoneId.of(DateTimeUtils.TZDB_REGION_CODE));
        OffsetDateTime threeThirtyAmTodayCanberraTime = OffsetDateTime.from(
                ZonedDateTime.of(nowCanberraTime.toLocalDate(),
                        runTime,
                        ZoneId.of(DateTimeUtils.TZDB_REGION_CODE)));
        OffsetDateTime nextScheduledTime = threeThirtyAmTodayCanberraTime.isAfter(nowCanberraTime) ? threeThirtyAmTodayCanberraTime : threeThirtyAmTodayCanberraTime.plusDays(1);
        long minutesToNextScheduledTime = Duration.between(nowCanberraTime, nextScheduledTime).toMinutes();
        scheduledExecutorService.scheduleAtFixedRate(detectSoPChanges(),
                minutesToNextScheduledTime,
                Duration.ofDays(1).toMinutes(),
                TimeUnit.MINUTES);
    }

    //todo: scheduled task to refresh cache of SoPs from Repository

    private static void setResponseHeaders(Response response, Boolean isJson, Integer statusCode) {
        response.status(statusCode);
        if (isJson) {
            response.type("application/json; charset=utf-8");
        } else {
            response.type("text/plain; charset=utf-8");
        }

        response.header("X-Content-Type-Options", "nosniff");

    }

    private static boolean responseTypeAcceptable(Request request) {
        String contentTypeHeader = request.headers("Accept");
        if (contentTypeHeader == null)
            return false;
        if (contentTypeHeader.contains("application/json"))
            return true;
        else return false;
    }

    private static Optional<String> generateSchemaForSopSupportRequestDto() {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);
        try {
            JsonSchema schema = schemaGen.generateSchema(SopSupportRequestDto.class);
            String schemaString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schema);
            return Optional.of(schemaString);
        } catch (JsonMappingException e) {
            logger.error("Failed to generate schema for request DTO for SoP Support Service.");
            return Optional.empty();
        } catch (JsonProcessingException e) {
            logger.error("Failed to generate schema for request DTO for SoP Support Service.");
            return Optional.empty();
        }
    }

    private static String buildAcceptableContentTypesError() {
        return "Accept header in request must include 'application/json'.";
    }

    private static Boolean validateHeaders() {
        return AppSettings.getEnvironment() == AppSettings.Environment.prod;
    }
}
