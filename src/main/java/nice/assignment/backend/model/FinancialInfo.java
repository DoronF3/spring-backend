package nice.assignment.backend.model;

import jakarta.validation.constraints.NotEmpty;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@Builder
public class FinancialInfo {

    @NotEmpty
    @NotNull
    private double cash;

    @NotEmpty
    @NotNull
    private int numberOfAssets;
}
