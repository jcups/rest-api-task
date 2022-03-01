package ru.jcups.restapitask.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.jcups.restapitask.model.Bucket;
import ru.jcups.restapitask.model.Role;
import ru.jcups.restapitask.model.User;
import ru.jcups.restapitask.repository.UserRepository;
import ru.jcups.restapitask.service.BucketService;
import ru.jcups.restapitask.service.UserService;

import java.util.List;
import java.util.Set;

@Service
public class DefaultUserService extends DefaultCrudService<User> implements UserDetailsService, UserService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultUserService.class);

    private final UserRepository userRepository;
    private final BucketService bucketService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public DefaultUserService(UserRepository userRepository, BucketService bucketService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        super(userRepository);
        this.userRepository = userRepository;
        this.bucketService = bucketService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User create(User user) {
        logger.info("DefaultUserService.create");
        logger.info("create() called with: user = [" + user + "]");
        if (user.getRoles() == null)
            user.setRoles(Set.of(Role.ROLE_USER));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        User created = userRepository.save(user);
        if (user.getBucket() == null) {
            bucketService.create(new Bucket(created, List.of()));
        }
        logger.debug("DefaultUserService.create() returned: " + created);
        return created;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("DefaultUserService.loadUserByUsername");
        logger.info("loadUserByUsername() called with: username = [" + username + "]");
        User user = userRepository.findUserByUsername(username);
        System.out.println(user);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        logger.debug("DefaultUserService.loadUserByUsername() returned: " + user);
        return user;
    }
}
