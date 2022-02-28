package ru.jcups.restapitask.controller.rest;

import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
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

    private final UserService userService;

    @PostMapping("")
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        System.out.println("UserController.createUser");
        System.out.println("user = " + user);
        User created = userService.create(user);
        return new ResponseEntity<>(created, HttpStatus.CREATED);

    }

    @PutMapping("")
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        System.out.println("UserController.updateUser");
        System.out.println("user = " + user);
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
        System.out.println("UserController.deleteById");
        System.out.println("id = " + id);
        try {
            return userService.deleteById(id) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("")
    public ResponseEntity<List<User>> getAll() {
        System.out.println("UserController.getAll");
        try {
            return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable long id) {
        System.out.println("UserController.getById");
        System.out.println("id = " + id);
        try {
            User found = userService.getById(id);
            return new ResponseEntity<>(found, HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
