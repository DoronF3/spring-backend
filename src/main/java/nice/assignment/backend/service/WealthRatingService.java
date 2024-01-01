package nice.assignment.backend.service;

import nice.assignment.backend.model.Person;
import nice.assignment.backend.model.RichPerson;
import nice.assignment.backend.repo.RichPersonRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;


@Service
public class WealthRatingService {

    static final String RICH_WRITTEN_TO_DB_MESSAGE = "Evaluation completed, the person is rich and was written to DB";
    static final String NOT_RICH_NOT_WRITTEN_TO_DB_MESSAGE = "Evaluation completed, the person is not rich and was not written to DB";
    static final String ASSET_EVALUATION_URL = "http://central-bank/regional-info/evaluate?city=";
    static final String WEALTH_THRESHOLD_URL = "http://central-bank/wealth-threshold";

    private final RichPersonRepository richPersonRepository;
    private final RestTemplate restTemplate;

    public WealthRatingService(RichPersonRepository richPersonRepository, RestTemplate restTemplate) {
        this.richPersonRepository = richPersonRepository;
        this.restTemplate = restTemplate;
    }

    public String evaluateWealth(Person person) {
        return isRich(person) ? writeToDB(person) : NOT_RICH_NOT_WRITTEN_TO_DB_MESSAGE;
    }

    private String writeToDB(Person person) {
        RichPerson richPerson = RichPerson.builder()
                .id(person.getId())
                .firstName(person.getPersonalInfo().getFirstName())
                .lastName(person.getPersonalInfo().getLastName())
                .fortune(calculateFortune(person)).build();

        richPersonRepository.save(richPerson);
        return RICH_WRITTEN_TO_DB_MESSAGE;
    }

    private boolean isRich(Person person) {
        double wealthThreshold = getWealthThreshold();
        double fortune = calculateFortune(person);
        return fortune > wealthThreshold;
    }

    private double calculateFortune(Person person) {
        double assetEvaluation = getAssetEvaluation(person.getPersonalInfo().getCity());
        int numOfAssets = person.getFinancialInfo().getNumberOfAssets();
        double cash = person.getFinancialInfo().getCash();
        return cash + numOfAssets * assetEvaluation;
    }

    private double getAssetEvaluation(String city) {
        ResponseEntity<Double> response = restTemplate.getForEntity(ASSET_EVALUATION_URL + city, Double.class);
        return response.getBody();
    }

    private double getWealthThreshold() {

        ResponseEntity<Double> response = restTemplate.getForEntity(WEALTH_THRESHOLD_URL, Double.class);
        return response.getBody();
    }

    public List<RichPerson> getAllRichPeople() {
        return (List<RichPerson>) richPersonRepository.findAll();
    }

    public Optional<RichPerson> getRichPersonById(Long id) {
        return richPersonRepository.findById(id);
    }
}
