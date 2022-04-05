package ru.jcups.restapitask.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.jcups.restapitask.model.Role;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleDTO {
	Role role;
	RoleAction action;
}
