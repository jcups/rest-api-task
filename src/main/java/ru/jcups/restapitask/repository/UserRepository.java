package ru.jcups.restapitask.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.jcups.restapitask.model.Role;
import ru.jcups.restapitask.model.User;

import java.util.Set;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    User findUserByUsername (String username);
    User findUserByToken(String token);
}
