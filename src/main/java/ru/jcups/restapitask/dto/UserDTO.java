package ru.jcups.restapitask.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.jcups.restapitask.model.Role;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserDTO {

    long id;
    String firstName;
    String lastName;
    int age;
    String email;
    String username;
    Set<Role> roles;
}
