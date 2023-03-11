package chipization.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    @NotNull
    @Column(name = "first_name")
    private String firstName;

    @NotBlank
    @NotNull
    @Column(name = "last_name")
    private String lastName;

    @Email
    @NotBlank
    @NotNull
    private String email;

    @NotBlank
    @NotNull
    private String password;
}
