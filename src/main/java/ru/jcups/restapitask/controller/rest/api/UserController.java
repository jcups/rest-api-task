package ru.jcups.restapitask.controller.rest.api;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.jcups.restapitask.dto.RoleAction;
import ru.jcups.restapitask.dto.RoleDTO;
import ru.jcups.restapitask.dto.UserDTO;
import ru.jcups.restapitask.model.Role;
import ru.jcups.restapitask.model.User;
import ru.jcups.restapitask.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	private final UserService userService;
	private final ModelMapper mapper;

	@GetMapping("/roles")
	public ResponseEntity<Set<Role>> getMyAuthorities(@RequestHeader("X-App-Token") String token) {
		Set<Role> authorities = userService.findUserByToken(token).getRoles();
		if (authorities != null && !authorities.isEmpty())
			return ResponseEntity.ok(authorities);
		else
			return ResponseEntity.noContent().build();
	}


	@PostMapping("")
	public ResponseEntity<UserDTO> createUser(@Valid @RequestBody User user, @RequestHeader("X-App-Token") String token) {
		if (user.isPasswordConfirmed()) {
			if (user.getRoles() == null)
				user.setRoles(Set.of(Role.ROLE_USER));
			if (hasAccess(user, token)) {
				User created = userService.create(user);
				return new ResponseEntity<>(mapper.map(created, UserDTO.class), HttpStatus.CREATED);
			} else
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	@PutMapping("")
	public ResponseEntity<UserDTO> updateUser(@RequestBody User user, @RequestHeader("X-App-Token") String token) {
		if (hasAccess(user, token)) {
			User updated = userService.update(user);
			return new ResponseEntity<>(mapper.map(updated, UserDTO.class), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteById(@PathVariable long id, @RequestHeader("X-App-Token") String token) {
		if (hasAccess(userService.getById(id), token)) {
			return userService.deleteById(id) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
		} else
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}

	@GetMapping("")
	public ResponseEntity<Page<UserDTO>> getAll(Pageable pageable, @RequestHeader("X-App-Token") String token) {
		if (userService.findUserByToken(token).getMaxPriorityRole().ordinal() >= Role.ROLE_ADMIN.ordinal()) {
			Page<User> users = userService.findAllAtPage(pageable.getPageNumber(), pageable.getPageSize());
			List<UserDTO> usersDTOs = users.stream().map(user -> mapper.map(user, UserDTO.class)).collect(Collectors.toList());
			Page<UserDTO> page = new PageImpl<>(usersDTOs, users.getPageable(), users.getTotalElements());
			return ResponseEntity.ok(page);
		} else
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserDTO> getById(@PathVariable long id, @RequestHeader("X-App-Token") String token) {
		User found = userService.getById(id);
		if (hasAccess(found, token))
			return new ResponseEntity<>(mapper.map(found, UserDTO.class), HttpStatus.OK);
		else
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}

	@PatchMapping("/{id}/roles")
	public ResponseEntity<?> actionRole(@RequestBody RoleDTO role, @PathVariable long id, @RequestHeader("X-App-Token") String token) {
		User changed = userService.getById(id);
		if (role.getAction().equals(RoleAction.APPEND))
			changed.getRoles().add(role.getRole());
		if (hasAccess(changed, token)) {
			Role roleVal = role.getRole();
			RoleAction action = role.getAction();
			if (action.equals(RoleAction.APPEND)) {
				userService.appendRoleToUser(id, roleVal);
			} else if (action.equals(RoleAction.DELETE)) {
				userService.deleteRoleOfUser(id, roleVal);
			} else {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	private boolean hasAccess(User user, String token) {
		Role priorityRole = userService.findUserByToken(token).getMaxPriorityRole();
		return priorityRole.ordinal() > user.getMaxPriorityRole().ordinal();
	}
}
