package ru.jcups.restapitask.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.jcups.restapitask.exceptions.TokenNotFoundException;
import ru.jcups.restapitask.model.Role;
import ru.jcups.restapitask.model.User;
import ru.jcups.restapitask.repository.UserRepository;
import ru.jcups.restapitask.service.UserService;

import java.util.Set;

@Service
public class DefaultUserService extends DefaultCrudService<User> implements UserDetailsService, UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public DefaultUserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
		super(userRepository);
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	public Page<User> findAllAtPage(int page, int quantity) {
		Pageable pageable = PageRequest.of(page, quantity);
		return userRepository.findAll(pageable);
	}

	@Override
	public User create(User user) {
		if (user.getRoles() == null)
			user.setRoles(Set.of(Role.ROLE_USER));
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	public boolean isUsernameNotUsed(String username) {
		return userRepository.findUserByUsername(username) == null;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findUserByUsername(username);
		System.out.println(user);
		if (user == null) {
			throw new UsernameNotFoundException("User not found");
		}
		return user;
	}

	@Override
	public User findUserByToken(String token) {
		User user = userRepository.findUserByToken(token);
		if (user == null) {
			throw new TokenNotFoundException("User by token = " + token + ", not found");
		}
		return user;
	}

	@Override
	public void appendRoleToUser(long id, Role role) {
		User user = this.getById(id);
		user.getRoles().add(role);
		this.update(user);
	}

	@Override
	public void deleteRoleOfUser(long id, Role roleVal) {
		User user = this.getById(id);
		user.getRoles().remove(roleVal);
		this.update(user);
	}
}
