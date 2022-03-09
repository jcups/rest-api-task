package ru.jcups.restapitask.controller.rest.api;

import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import ru.jcups.restapitask.dto.RoleAction;
import ru.jcups.restapitask.dto.RoleDTO;
import ru.jcups.restapitask.dto.UserDTO;
import ru.jcups.restapitask.model.Role;
import ru.jcups.restapitask.model.User;
import ru.jcups.restapitask.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	private final UserService userService;
	private final ModelMapper mapper;

	@GetMapping("/roles")
	public ResponseEntity<Collection<? extends GrantedAuthority>> getMyAuthorities(Authentication auth) {
		if (auth != null) {
			Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
			if (authorities != null && !authorities.isEmpty())
				return ResponseEntity.ok(authorities.stream().sorted().collect(Collectors.toList()));
			else
				return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@PostMapping("")
	public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
		logger.info("UserController.createUser");
		logger.info("createUser() called with: user = [" + user + "]");
		User created = userService.create(user);
		return new ResponseEntity<>(created, HttpStatus.CREATED);
	}

	@PutMapping("")
	public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
		logger.info("UserController.updateUser");
		logger.info("updateUser() called with: user = [" + user + "]");
		try {
			User updated = userService.update(user);
			return new ResponseEntity<>(updated, HttpStatus.OK);
		} catch (ObjectNotFoundException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteById(@PathVariable long id) {
		logger.info("UserController.deleteById");
		logger.info("deleteById() called with: id = [" + id + "]");
		try {
			return userService.deleteById(id) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
		} catch (ObjectNotFoundException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("")
	public ResponseEntity<Page<UserDTO>> getAll(Pageable pageable) {
		logger.info("UserController.getAll");
		logger.info("getAll() called with: pageable = [" + pageable + "]");
		try {
			Page<User> users = userService.findAllAtPage(pageable.getPageNumber(), pageable.getPageSize());
			List<UserDTO> usersDTOs = users.stream().map(user -> mapper.map(user, UserDTO.class)).collect(Collectors.toList());
			Page<UserDTO> page = new PageImpl<>(usersDTOs, users.getPageable(), users.getTotalElements());
			return ResponseEntity.ok(page);
		} catch (ObjectNotFoundException e) {
			e.printStackTrace();
			return ResponseEntity.noContent().build();
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserDTO> getById(@PathVariable long id) {
		logger.info("UserController.getById");
		logger.info("getById() called with: id = [" + id + "]");
		try {
			User found = userService.getById(id);
			UserDTO dto = mapper.map(found, UserDTO.class);
			return new ResponseEntity<>(dto, HttpStatus.OK);
		} catch (ObjectNotFoundException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PatchMapping("/{id}/roles")
	public ResponseEntity<?> actionRole(@RequestBody RoleDTO role, @PathVariable long id, Authentication auth) {
		if (auth != null && auth.isAuthenticated()) {
			Collection<? extends GrantedAuthority> roles = auth.getAuthorities();
			Role roleVal = role.getRole();
			if (roles.contains(Role.ROLE_CREATOR)) {
				if (!roleVal.equals(Role.ROLE_CREATOR)) {
					if (role.getAction().equals(RoleAction.APPEND)) {
						userService.appendRoleToUser(id, roleVal);
					} else if (role.getAction().equals(RoleAction.DELETE)) {
						userService.deleteRoleOfUser(id, roleVal);
					} else {
						return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
					}
				}
			} else if (roles.contains(Role.ROLE_ADMIN)) {
				if (roleVal.equals(Role.ROLE_MODERATOR)) {
					if (role.getAction().equals(RoleAction.APPEND)) {
						userService.appendRoleToUser(id, roleVal);
					} else if (role.getAction().equals(RoleAction.DELETE)) {
						userService.deleteRoleOfUser(id, roleVal);
					} else {
						return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
					}
				}
			}
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}
}
