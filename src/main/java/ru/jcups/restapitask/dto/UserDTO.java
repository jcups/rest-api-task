package ru.jcups.restapitask.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.jcups.restapitask.model.Role;
import ru.jcups.restapitask.model.User;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserDTO {
	long id;
	String email;
	String username;
	Set<Role> roles;
	@JsonIgnoreProperties("user")
	User.Info info;
}
