package nice.assignment.backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nice.assignment.backend.model.FinancialInfo;
import nice.assignment.backend.model.Person;
import nice.assignment.backend.model.PersonalInfo;
import nice.assignment.backend.model.RichPerson;
import nice.assignment.backend.repo.RichPersonRepository;
import nice.assignment.backend.service.WealthRatingService;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
;

@SpringBootTest
public class WealthRatingEvaluateEndpointTest {

    private static final String RICH_WRITTEN_TO_DB_MESSAGE_TEST = "Evaluation completed, the person is rich and was written to DB";
    private static final String NOT_RICH_NOT_WRITTEN_TO_DB_MESSAGE_TEST = "Evaluation completed, the person is not rich and was not written to DB";
    private static final String ASSET_EVALUATION_URL_TEST = "http://central-bank/regional-info/evaluate?city=";
    private static final String WEALTH_THRESHOLD_URL_TEST = "http://central-bank/wealth-threshold";

    @Mock
    private RichPersonRepository richPersonRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WealthRatingService wealthRatingService;


    @Test
    void testEvaluateWealthAndWriteToDB() {
        JsonNode person = createMockRichPersonJson();

        when(restTemplate.getForEntity(eq(ASSET_EVALUATION_URL_TEST + person.get("personalInfo").get("city").asText()), eq(Double.class)))
                .thenReturn(new ResponseEntity<>(50.0, HttpStatus.OK));

        when(restTemplate.getForEntity(eq(WEALTH_THRESHOLD_URL_TEST), eq(Double.class)))
                .thenReturn(new ResponseEntity<>(50.0, HttpStatus.OK));

        String result = wealthRatingService.evaluateWealth(person);

        assertEquals(RICH_WRITTEN_TO_DB_MESSAGE_TEST, result);
        verify(richPersonRepository, times(1)).save(any(RichPerson.class));
    }

    @Test
    void testEvaluateNotRichNoWriteToDB() {
        JsonNode person = createMockNotRichPersonJson();

        when(restTemplate.getForEntity(eq(ASSET_EVALUATION_URL_TEST + person.get("personalInfo").get("city").asText()), eq(Double.class)))
                .thenReturn(new ResponseEntity<>(50.0, HttpStatus.OK));

        when(restTemplate.getForEntity(eq(WEALTH_THRESHOLD_URL_TEST), eq(Double.class)))
                .thenReturn(new ResponseEntity<>(5000000000.0, HttpStatus.OK));

        String result = wealthRatingService.evaluateWealth(person);

        assertEquals(NOT_RICH_NOT_WRITTEN_TO_DB_MESSAGE_TEST, result);
        verify(richPersonRepository, times(0)).save(any(RichPerson.class));
    }

    @Test
    void testBuildPersonFromJson_WithNullValues_ShouldThrowException() {
        JsonNode person = createMockPersonWithNullValuesJson();

        String result = wealthRatingService.evaluateWealth(person);

        assertEquals("Invalid input format or missing required fields.", result);
    }

    @Test
    void testBuildPersonFromJson_WithMissingFields_ShouldThrowException() {
        JsonNode person = createMockPersonWithMissingFieldsJson();

        String result = wealthRatingService.evaluateWealth(person);

        assertEquals("Invalid input format or missing required fields.", result);
    }

    @Test
    void testBuildPersonFromJson_WithExtraFields_ShouldThrowException() {
        JsonNode person = createMockPersonWithExtraFieldsJson();

        String result = wealthRatingService.evaluateWealth(person);

        assertEquals("Invalid input format or missing required fields.", result);
    }

    @Test
    void testEvaluateWealth_NegativeId() {
        JsonNode person = createMockPersonWithNegativeIdJson();

        String result = wealthRatingService.evaluateWealth(person);

        assertEquals("Invalid input format or missing required fields.", result);
        verify(richPersonRepository, never()).save(any(RichPerson.class));
    }

    @Test
    void testEvaluateWealth_NegativeAssets() {
        JsonNode person = createMockPersonWithNegativeAssetsJson();

        String result = wealthRatingService.evaluateWealth(person);

        assertEquals("Invalid input format or missing required fields.", result);
        verify(richPersonRepository, never()).save(any(RichPerson.class));
    }

    @Test
    void testEvaluateWealth_WrongTypeField() {
        JsonNode person = createMockPersonWithWrongTypeFieldJson();

        String result = wealthRatingService.evaluateWealth(person);

        assertEquals("Invalid input format or missing required fields.", result);
        verify(richPersonRepository, never()).save(any(RichPerson.class));
    }


    private JsonNode createMockRichPersonJson() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = "{\n" +
                    "  \"id\": 123456789,\n" +
                    "  \"personalInfo\": {\n" +
                    "    \"firstName\": \"Bill\",\n" +
                    "    \"lastName\": \"Gates\",\n" +
                    "    \"city\": \"Washington\"\n" +
                    "  },\n" +
                    "  \"financialInfo\": {\n" +
                    "    \"cash\": 16000000000,\n" +
                    "    \"numberOfAssets\": 50\n" +
                    "  }\n" +
                    "}";
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException("Error creating mock JSON node", e);
        }
    }

    private JsonNode createMockNotRichPersonJson() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = "{\n" +
                    "  \"id\": 987654321,\n" +
                    "  \"personalInfo\": {\n" +
                    "    \"firstName\": \"John\",\n" +
                    "    \"lastName\": \"Doe\",\n" +
                    "    \"city\": \"New York\"\n" +
                    "  },\n" +
                    "  \"financialInfo\": {\n" +
                    "    \"cash\": 5000,\n" +
                    "    \"numberOfAssets\": 2\n" +
                    "  }\n" +
                    "}";
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException("Error creating mock JSON node", e);
        }
    }

    private JsonNode createMockPersonWithNullValuesJson() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = "{\n" +
                    "  \"id\": null,\n" +
                    "  \"personalInfo\": null,\n" +
                    "  \"financialInfo\": null\n" +
                    "}";
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException("Error creating mock JSON node", e);
        }
    }

    private JsonNode createMockPersonWithMissingFieldsJson() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = "{\n" +
                    "  \"id\": 123,\n" +
                    "  \"personalInfo\": {\n" +
                    "    \"firstName\": \"Alice\",\n" +
                    "    \"city\": \"London\"\n" +
                    "  }\n" +
                    "}";
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException("Error creating mock JSON node", e);
        }
    }

    private JsonNode createMockPersonWithExtraFieldsJson() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = "{\n" +
                    "  \"id\": 456,\n" +
                    "  \"personalInfo\": {\n" +
                    "    \"firstName\": \"Bob\",\n" +
                    "    \"lastName\": \"Smith\",\n" +
                    "    \"city\": \"Paris\",\n" +
                    "    \"extraField\": \"Extra\"\n" +
                    "  },\n" +
                    "  \"financialInfo\": {\n" +
                    "    \"cash\": 10000,\n" +
                    "    \"numberOfAssets\": 5,\n" +
                    "    \"extraField\": \"Extra\"\n" +
                    "  },\n" +
                    "  \"extraField\": \"Extra\"\n" +
                    "}";
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException("Error creating mock JSON node", e);
        }
    }

    private JsonNode createMockPersonWithNegativeIdJson() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = "{\n" +
                    "  \"id\": -123,\n" +
                    "  \"personalInfo\": {\n" +
                    "    \"firstName\": \"Eve\",\n" +
                    "    \"lastName\": \"Johnson\",\n" +
                    "    \"city\": \"Berlin\"\n" +
                    "  },\n" +
                    "  \"financialInfo\": {\n" +
                    "    \"cash\": 8000,\n" +
                    "    \"numberOfAssets\": 3\n" +
                    "  }\n" +
                    "}";
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException("Error creating mock JSON node", e);
        }
    }

    private JsonNode createMockPersonWithNegativeAssetsJson() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = "{\n" +
                    "  \"id\": 789,\n" +
                    "  \"personalInfo\": {\n" +
                    "    \"firstName\": \"Tom\",\n" +
                    "    \"lastName\": \"Williams\",\n" +
                    "    \"city\": \"Tokyo\"\n" +
                    "  },\n" +
                    "  \"financialInfo\": {\n" +
                    "    \"cash\": 12000,\n" +
                    "    \"numberOfAssets\": -2\n" +
                    "  }\n" +
                    "}";
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException("Error creating mock JSON node", e);
        }
    }

    private JsonNode createMockPersonWithWrongTypeFieldJson() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = "{\n" +
                    "  \"id\": 456,\n" +
                    "  \"personalInfo\": {\n" +
                    "    \"firstName\": \"Sophie\",\n" +
                    "    \"lastName\": 123,\n" +
                    "    \"city\": \"Sydney\"\n" +
                    "  },\n" +
                    "  \"financialInfo\": {\n" +
                    "    \"cash\": 15000,\n" +
                    "    \"numberOfAssets\": 4\n" +
                    "  }\n" +
                    "}";
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException("Error creating mock JSON node", e);
        }
    }
}
