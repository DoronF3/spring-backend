package nice.assignment.backend;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        WealthRatingEvaluateEndpointTest.class,
        WealthRatingGetAllRichPeopleEndpointTest.class,
        WealthRatingGetRichPersonByIdEndpointTest.class
})
public class WealthRatingTestSuite {
    // Todo fix the test suite to work
}
