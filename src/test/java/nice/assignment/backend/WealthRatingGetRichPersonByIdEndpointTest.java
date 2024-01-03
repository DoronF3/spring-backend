package nice.assignment.backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nice.assignment.backend.model.RichPerson;
import nice.assignment.backend.repo.RichPersonRepository;
import nice.assignment.backend.service.WealthRatingService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class WealthRatingGetRichPersonByIdEndpointTest {

    private static final String ASSET_EVALUATION_URL_TEST = "http://central-bank/regional-info/evaluate?city=";
    private static final String WEALTH_THRESHOLD_URL_TEST = "http://central-bank/wealth-threshold";

    @Mock
    private RichPersonRepository richPersonRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WealthRatingService wealthRatingService;

    @Test
    void testGetError() {
        String id = "123456789";
        String richPeople = wealthRatingService.getRichPersonById("123456789");

        assertEquals("Rich person with ID " + id + " not found.", richPeople);
    }

    @Test
    void testGetRichPersonOutOf1() {

        JsonNode person1 = createMockNotRichPersonJson1();

        when(restTemplate.getForEntity(eq(ASSET_EVALUATION_URL_TEST + person1.get("personalInfo").get("city").asText()), eq(Double.class)))
                .thenReturn(new ResponseEntity<>(50.0, HttpStatus.OK));

        when(restTemplate.getForEntity(eq(WEALTH_THRESHOLD_URL_TEST), eq(Double.class)))
                .thenReturn(new ResponseEntity<>(50.0, HttpStatus.OK));

        wealthRatingService.evaluateWealth(person1);

        String richPeople = wealthRatingService.getRichPersonById("123456789");

        assertEquals("RichPerson(id=123456789, firstName=bill, lastName=Gates, fortune=1.60000025E10)", richPeople);
    }

    @Test
    void testGetRichPersonOutOfTwo() {

        JsonNode person1 = createMockNotRichPersonJson1();

        JsonNode person2 = createMockNotRichPersonJson2();

        when(restTemplate.getForEntity(eq(ASSET_EVALUATION_URL_TEST + person1.get("personalInfo").get("city").asText()), eq(Double.class)))
                .thenReturn(new ResponseEntity<>(50.0, HttpStatus.OK));

        when(restTemplate.getForEntity(eq(WEALTH_THRESHOLD_URL_TEST), eq(Double.class)))
                .thenReturn(new ResponseEntity<>(50.0, HttpStatus.OK));

        wealthRatingService.evaluateWealth(person1);

        when(restTemplate.getForEntity(eq(ASSET_EVALUATION_URL_TEST + person2.get("personalInfo").get("city").asText()), eq(Double.class)))
                .thenReturn(new ResponseEntity<>(50.0, HttpStatus.OK));

        wealthRatingService.evaluateWealth(person2);

        String richPeople = wealthRatingService.getRichPersonById("123456789");

        assertEquals("RichPerson(id=123456789, firstName=bill, lastName=Gates, fortune=1.60000025E10)", richPeople);
    }

    private JsonNode createMockNotRichPersonJson1() {
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

    private JsonNode createMockNotRichPersonJson2() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = "{\n" +
                    "  \"id\": 87654321,\n" +
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
}
