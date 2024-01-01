package nice.assignment.backend.model;

import jakarta.validation.constraints.NotEmpty;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
public class PersonalInfo {

    @NotEmpty
    @NotNull
    @Length(min = 1, max = 30)
    private String firstName;

    @NotEmpty
    @NotNull
    @Length(min = 1, max = 30)
    private String lastName;

    @NotEmpty
    @NotNull
    @Length(min = 1, max = 30)
    private String city;
}
