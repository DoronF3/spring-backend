package nice.assignment.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import org.hibernate.validator.constraints.Length;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "rich_people")
public class RichPerson implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @NotNull
    @Length(min = 1, max = 30)
    private String firstName;

    @NotEmpty
    @NotNull
    @Length(min = 1, max = 30)
    private String lastName;

    @NotNull
    private double fortune;
}
