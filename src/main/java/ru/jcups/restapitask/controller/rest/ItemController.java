package ru.jcups.restapitask.controller.rest;

import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.jcups.restapitask.model.Guest;
import ru.jcups.restapitask.model.Item;
import ru.jcups.restapitask.model.User;
import ru.jcups.restapitask.service.ItemService;
import ru.jcups.restapitask.service.UserService;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemController {

    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

    private final ItemService itemService;
    private final UserService userService;
    private final HttpHeaders headers;

    @PatchMapping("/{id}")
    public ResponseEntity<?> rate(@PathVariable long id, @RequestParam("rate") float rate) {
        logger.info("ItemController.rate");
        logger.info("rate() called with: id = [" + id + "], rate = [" + rate + "]");
        itemService.newRate(id, rate);
        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    public ResponseEntity<List<Item>> getItems() {
        logger.info("ItemController.getItems");
        try {
            ResponseEntity<List<Item>> response = new ResponseEntity<>(itemService.getAll(),
                    headers, HttpStatus.OK);
            logger.debug("ItemController.getItems() returned: " + response);
            return response;
        } catch (ObjectNotFoundException e) {
            logger.error("ItemController.getItems: ", e);
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> findById(@PathVariable long id) {
        logger.info("ItemController.findById");
        logger.info("findById() called with: id = [" + id + "]");
        try {
            return new ResponseEntity<>(itemService.getById(id),
                    headers, HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/add/{id}")
    public ResponseEntity<?> addToBucket(@PathVariable long id, Authentication authentication, HttpSession session,
                                         @RequestParam(value = "quantity", required = false, defaultValue = "1") int quantity) {
        logger.info("ItemController.addToBucket");
        logger.info("addToBucket() called with: id = [" + id + "], authentication = [" + authentication +
                "], session = [" + session + "], quantity = [" + quantity + "]");
        if (authentication != null) {
            return processAuthenticated(id, authentication, quantity);
        } else if (session != null) {
            return processGuest(id, session, quantity);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    private ResponseEntity<Object> processGuest(long id, HttpSession session, int quantity) {
        logger.info("ItemController.processGuest");
        logger.info("processGuest() called with: id = [" + id + "], session = [" + session + "], quantity = [" + quantity + "]");
        try {
            Guest guest = (Guest) session.getAttribute("guest");
            if (guest == null) {
                guest = new Guest();
                session.setAttribute("guest", guest);
            }
            guest.addItemToBucket(itemService.getById(id), quantity);
            return ResponseEntity.ok().build();
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    private ResponseEntity<Object> processAuthenticated(long id, Authentication authentication, int quantity) {
        logger.info("ItemController.processAuthenticated");
        logger.info("processAuthenticated() called with: id = [" + id +
                "], authentication = [" + authentication + "], quantity = [" + quantity + "]");
        try {
            User user = (User) authentication.getPrincipal();
            if (user != null) {
                itemService.addToBucket(id, user, quantity);
                return ResponseEntity.ok().build();
            } else
                return ResponseEntity.badRequest().build();
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }
}
