package ru.jcups.restapitask.controller.rest;

import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.jcups.restapitask.model.User;
import ru.jcups.restapitask.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    // TODO: 01.03.2022 Сделать страницу Users доступную Админу и создателю
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
    public ResponseEntity<List<User>> getAll() {
        logger.info("UserController.getAll");
        try {
            return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable long id) {
        logger.info("UserController.getById");
        logger.info("getById() called with: id = [" + id + "]");
        try {
            User found = userService.getById(id);
            return new ResponseEntity<>(found, HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
