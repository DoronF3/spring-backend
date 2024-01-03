package nice.assignment.backend.repo;

import nice.assignment.backend.model.RichPerson;
import org.springframework.data.repository.CrudRepository;

public interface RichPersonRepository extends CrudRepository<RichPerson,Long> {
}
