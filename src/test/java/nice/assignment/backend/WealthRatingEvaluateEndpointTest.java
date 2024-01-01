package nice.assignment.backend;

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
        Person person = createSamplePerson();

        when(restTemplate.getForEntity(eq(ASSET_EVALUATION_URL_TEST + person.getPersonalInfo().getCity()), eq(Double.class)))
                .thenReturn(new ResponseEntity<>(50.0, HttpStatus.OK));

        when(restTemplate.getForEntity(eq(WEALTH_THRESHOLD_URL_TEST), eq(Double.class)))
                .thenReturn(new ResponseEntity<>(50.0, HttpStatus.OK));

        String result = wealthRatingService.evaluateWealth(person);

        assertEquals(RICH_WRITTEN_TO_DB_MESSAGE_TEST, result);
        verify(richPersonRepository, times(1)).save(any(RichPerson.class));
    }

    @Test
    void testEvaluateWealthNotRich() {
        Person person = createSamplePerson();

        when(restTemplate.getForEntity(eq(ASSET_EVALUATION_URL_TEST + person.getPersonalInfo().getCity()), eq(Double.class)))
                .thenReturn(new ResponseEntity<>(1.0, HttpStatus.OK));

        when(restTemplate.getForEntity(eq(WEALTH_THRESHOLD_URL_TEST), eq(Double.class)))
                .thenReturn(new ResponseEntity<>(20000000.0, HttpStatus.OK));

        String result = wealthRatingService.evaluateWealth(person);

        assertEquals(NOT_RICH_NOT_WRITTEN_TO_DB_MESSAGE_TEST, result);
        verify(richPersonRepository, never()).save(any(RichPerson.class));
    }

    @Test
    void testEvaluateWealthNotRich_NegativeCash() {
        Person person = createSamplePersonWithNegativeCash();

        when(restTemplate.getForEntity(eq(ASSET_EVALUATION_URL_TEST + person.getPersonalInfo().getCity()), eq(Double.class)))
                .thenReturn(new ResponseEntity<>(50.0, HttpStatus.OK));

        when(restTemplate.getForEntity(eq(WEALTH_THRESHOLD_URL_TEST), eq(Double.class)))
                .thenReturn(new ResponseEntity<>(50.0, HttpStatus.OK));

        String result = wealthRatingService.evaluateWealth(person);

        assertEquals(NOT_RICH_NOT_WRITTEN_TO_DB_MESSAGE_TEST, result);
        verify(richPersonRepository, never()).save(any(RichPerson.class));
    }

    @Test
    void testEvaluateWealthNotRich_ZeroAssets() {
        Person person = createSamplePersonWithZeroAssets();

        when(restTemplate.getForEntity(eq(ASSET_EVALUATION_URL_TEST + person.getPersonalInfo().getCity()), eq(Double.class)))
                .thenReturn(new ResponseEntity<>(50.0, HttpStatus.OK));

        when(restTemplate.getForEntity(eq(WEALTH_THRESHOLD_URL_TEST), eq(Double.class)))
                .thenReturn(new ResponseEntity<>(50.0, HttpStatus.OK));

        String result = wealthRatingService.evaluateWealth(person);

        assertEquals(NOT_RICH_NOT_WRITTEN_TO_DB_MESSAGE_TEST, result);
        verify(richPersonRepository, never()).save(any(RichPerson.class));
    }


    private Person createSamplePerson() {
        return Person.builder()
                .id(1L)
                .personalInfo(new PersonalInfo("John", "Doe", "City"))
                .financialInfo(new FinancialInfo(100.0, 10))
                .build();
    }
    private Person createSamplePersonWithNegativeCash() {
        return Person.builder()
                .id(1L)
                .personalInfo(new PersonalInfo("John", "Doe", "City"))
                .financialInfo(new FinancialInfo(-100.0, 1))
                .build();
    }

    private Person createSamplePersonWithZeroAssets() {
        return Person.builder()
                .id(1L)
                .personalInfo(new PersonalInfo("John", "Doe", "City"))
                .financialInfo(new FinancialInfo(0.0, 0))
                .build();
    }
}
