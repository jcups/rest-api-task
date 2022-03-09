package ru.jcups.restapitask.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public Page<User> findAllAtPage(int page, int quantity){
        logger.info("DefaultUserService.findAllAtPage");
        logger.info("findAllAtPage() called with: page = [" + page + "], quantity = [" + quantity + "]");
        Pageable pageable = PageRequest.of(page, quantity);
        return userRepository.findAll(pageable);
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

    public boolean isUsernameNotUsed(String username){
        logger.info("DefaultUserService.ifExistsByUsername");
        logger.info("ifExistsByUsername() called with: username = [" + username + "]");
        return userRepository.findUserByUsername(username) == null;
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

    @Override
    public void appendRoleToUser(long id, Role role) {
        logger.info("DefaultUserService.appendRoleToUser");
        logger.info("appendRoleToUser() called with: id = [" + id + "], role = [" + role + "]");
        User user = this.getById(id);
        user.getRoles().add(role);
        this.update(user);
    }

    @Override
    public void deleteRoleOfUser(long id, Role roleVal) {
        logger.info("DefaultUserService.deleteRoleOfUser");
        logger.info("deleteRoleOfUser() called with: id = [" + id + "], roleVal = [" + roleVal + "]");
        User user = this.getById(id);
        user.getRoles().remove(roleVal);
        this.update(user);
    }
}
