package nice.assignment.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import nice.assignment.backend.model.RichPerson;
import nice.assignment.backend.service.WealthRatingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wealth-rating")
public class WealthRatingController {
    private final WealthRatingService wealthRatingService;

    @Autowired
    public WealthRatingController(WealthRatingService wealthRatingService) {
        this.wealthRatingService = wealthRatingService;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<String> checkIfRichWriteRichToDB(@RequestBody JsonNode body) {
        return ResponseEntity.ok(wealthRatingService.evaluateWealth(body));
    }

    @GetMapping("/rich-people")
    public ResponseEntity<List<RichPerson>> getAllRichPeople() {
        return ResponseEntity.ok(wealthRatingService.getAllRichPeople());
    }

    @GetMapping("/rich-people/{id}")
    public ResponseEntity<String> getRichPersonById(@PathVariable String id) {
        return ResponseEntity.ok(wealthRatingService.getRichPersonById(id));
    }
}

