package nice.assignment.backend;

import nice.assignment.backend.model.RichPerson;
import nice.assignment.backend.repo.RichPersonRepository;
import nice.assignment.backend.service.WealthRatingService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class WealthRatingGetRichPersonByIdEndpointTest {

    @Mock
    private RichPersonRepository richPersonRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WealthRatingService wealthRatingService;

    @Test
    void testGetRichPersonById() {
        // Arrange
        long id = 123L;
        when(richPersonRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<RichPerson> richPerson = wealthRatingService.getRichPersonById(id);

        // Assert
        assertEquals(Optional.empty(), richPerson);
    }

    // Todo add more tests
}
