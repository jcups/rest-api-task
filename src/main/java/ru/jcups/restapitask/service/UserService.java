package ru.jcups.restapitask.service;

import org.springframework.data.domain.Page;
import ru.jcups.restapitask.model.Role;
import ru.jcups.restapitask.model.User;

public interface UserService extends CrudService<User> {

	User findUserByToken(String token);

	void appendRoleToUser(long id, Role role);

	void deleteRoleOfUser(long id, Role roleVal);

	Page<User> findAllAtPage(int page, int quantity);
}
