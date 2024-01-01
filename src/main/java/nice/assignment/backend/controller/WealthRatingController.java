package nice.assignment.backend.controller;

import nice.assignment.backend.model.Person;
import nice.assignment.backend.model.RichPerson;
import nice.assignment.backend.service.WealthRatingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/wealth-rating")
public class WealthRatingController {
    // Todo check what happens with wrong inputs and decide how to deal
    private final WealthRatingService wealthRatingService;

    @Autowired
    public WealthRatingController(WealthRatingService wealthRatingService) {
        this.wealthRatingService = wealthRatingService;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<String> checkIfRichWriteRichToDB(@RequestBody Person person) {
        return ResponseEntity.ok(wealthRatingService.evaluateWealth(person));
    }

    @GetMapping("/rich-people")
    public ResponseEntity<List<RichPerson>> getAllRichPeople() {
        return ResponseEntity.ok(wealthRatingService.getAllRichPeople());
    }

    @GetMapping("/rich-people/{id}")
    public ResponseEntity<Optional<RichPerson>> getRichPersonById(@PathVariable Long id) {
        return ResponseEntity.ok(wealthRatingService.getRichPersonById(id));
    }
}

