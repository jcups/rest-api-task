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
import ru.jcups.restapitask.model.Bucket;
import ru.jcups.restapitask.model.Guest;
import ru.jcups.restapitask.model.Item;
import ru.jcups.restapitask.model.User;
import ru.jcups.restapitask.service.BucketService;
import ru.jcups.restapitask.service.ItemService;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bucket")
public class BucketController {

    private static final Logger logger = LoggerFactory.getLogger(BucketController.class);

    private final BucketService bucketService;
    private final ItemService itemService;
    private final HttpHeaders headers;

    private Bucket cachedBucketForDebug;

    @GetMapping
    public ResponseEntity<?> getBucket(Authentication authentication, HttpSession session) {
        logger.info("BucketController.getBucket");
        logger.info("getBucket() called with: authentication = [" + authentication + "], session = [" + session + "]");
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            return processGetAuthenticatedBucket(user);
        } else if (session != null) {
            return processGetGuestBucket(session);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFromBucket(@PathVariable long id, Authentication authentication, HttpSession session) {
        logger.info("BucketController.deleteFromBucket");
        logger.info("deleteFromBucket() called with: id = [" + id + "], authentication = [" + authentication + "], session = [" + session + "]");
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            if (user != null)
                return processDeleteFromAuthenticationBucket(user, id);
            else
                return ResponseEntity.badRequest().build();
        } else if (session != null) {
            Guest guest = (Guest) session.getAttribute("guest");
            if (guest == null){
                session.setAttribute("guest", new Guest());
                guest = (Guest) session.getAttribute("guest");
            }
            return processDeleteFromGuestBucket(guest, id);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    private ResponseEntity<?> processDeleteFromGuestBucket(Guest guest, long id) {
        logger.info("BucketController.processDeleteFromGuestBucket");
        logger.info("processDeleteFromGuestBucket() called with: guest = [" + guest + "], id = [" + id + "]");
        Bucket bucket = guest.getBucket();
        if (bucket == null)
            return ResponseEntity.badRequest().build();
        List<Item> items = bucket.getItems();
        if (items == null)
            return ResponseEntity.badRequest().build();
        bucket.setItems(deleteItemWithId(items, id));
        guest.setBucket(bucket);
        return ResponseEntity.ok().build();
    }

    private List<Item> deleteItemWithId(List<Item> items, long id) {
        return items.stream()
                .filter(item -> item.getId() != id)
                .collect(Collectors.toList());
    }

    private ResponseEntity<?> processDeleteFromAuthenticationBucket(User user, long id) {
        logger.info("BucketController.processDeleteFromAuthenticationBucket");
        logger.info("processDeleteFromAuthenticationBucket() called with: user = [" + user + "], id = [" + id + "]");
        Bucket bucket = user.getBucket();
        if (bucket == null)
            return ResponseEntity.badRequest().build();
        List<Item> items = bucket.getItems();
        if (items == null)
            return ResponseEntity.badRequest().build();
        bucket.setItems(deleteItemWithId(items, id));
        bucketService.update(bucket);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> addToBucket(@PathVariable long id, Authentication authentication, HttpSession session,
                                         @RequestParam(value = "quantity", required = false, defaultValue = "1") int quantity) {
        logger.info("ItemController.addToBucket");
        logger.info("addToBucket() called with: id = [" + id + "], authentication = [" + authentication +
                "], session = [" + session + "], quantity = [" + quantity + "]");
        if (authentication != null) {
            return processAddToAuthenticatedBucket(id, authentication, quantity);
        } else if (session != null) {
            return processAddToGuestBucket(id, session, quantity);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    private ResponseEntity<Object> processAddToGuestBucket(long id, HttpSession session, int quantity) {
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

    private ResponseEntity<Object> processAddToAuthenticatedBucket(long id, Authentication authentication, int quantity) {
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

    @GetMapping("/d")
    public ResponseEntity<?> getBucketForDebug() {
        logger.info("BucketController.getBucketForDebug");
        if (cachedBucketForDebug == null) {
            return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(cachedBucketForDebug, headers, HttpStatus.OK);
    }

    private ResponseEntity<?> processGetGuestBucket(HttpSession session) {
        logger.info("BucketController.processGuest");
        logger.info("processGuest() called with: session = [" + session + "]");
        Guest guest = (Guest) session.getAttribute("guest");
        if (guest != null) {
            Bucket bucket = guest.getBucket();
            if (bucket != null) {
                cachedBucketForDebug = bucket;
                return new ResponseEntity<>(bucket, headers, HttpStatus.OK);
            } else {
                return ResponseEntity.noContent().build();
            }
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    private ResponseEntity<?> processGetAuthenticatedBucket(User user) {
        logger.info("BucketController.processAuthenticated");
        logger.info("processAuthenticated() called with: user = [" + user + "]");
        if (user.getId() != 0) {
            Bucket bucket = user.getBucket();
            if (bucket != null) {
                cachedBucketForDebug = bucketService.getById(bucket.getId());
                return new ResponseEntity<>(bucket, headers, HttpStatus.OK);
            } else
                return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
