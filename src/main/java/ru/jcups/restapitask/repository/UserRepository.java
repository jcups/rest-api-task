package ru.jcups.restapitask.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.jcups.restapitask.model.User;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    User findUserByUsername (String username);
}
