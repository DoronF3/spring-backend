package nice.assignment.backend;

import nice.assignment.backend.model.RichPerson;
import nice.assignment.backend.repo.RichPersonRepository;
import nice.assignment.backend.service.WealthRatingService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class WealthRatingGetAllRichPeopleEndpointTest {

    @Mock
    private RichPersonRepository richPersonRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WealthRatingService wealthRatingService;

    @Test
    void testGetAllRichPeople() {
        // Arrange
        when(richPersonRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<RichPerson> richPeople = wealthRatingService.getAllRichPeople();

        // Assert
        assertEquals(Collections.emptyList(), richPeople);
    }

    // Todo add more tests
}
