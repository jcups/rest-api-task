package ru.jcups.restapitask.repository;

import org.springframework.data.repository.CrudRepository;
import ru.jcups.restapitask.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
    User findUserByUsername (String username);
}
