package nice.assignment.backend.model;

import jakarta.validation.constraints.NotEmpty;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;


@Data
@AllArgsConstructor
@Builder
public class Person {
    @NotNull
    @NotEmpty
    private Long id;

    @NotNull
    @NotEmpty
    private PersonalInfo personalInfo;

    @NotNull
    @NotEmpty
    private FinancialInfo financialInfo;
}
