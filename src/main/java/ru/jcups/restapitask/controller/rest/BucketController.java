package ru.jcups.restapitask.controller.rest;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.jcups.restapitask.model.Bucket;
import ru.jcups.restapitask.model.Guest;
import ru.jcups.restapitask.model.User;
import ru.jcups.restapitask.service.BucketService;

import javax.servlet.http.HttpSession;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bucket")
public class BucketController {

    private static final Logger logger = LoggerFactory.getLogger(BucketController.class);

    private final BucketService bucketService;
    private final HttpHeaders headers;

    private Bucket cachedBucketForDebug;

    @GetMapping
    public ResponseEntity<?> getBucket(Authentication authentication, HttpSession session) {
        logger.info("BucketController.getBucket");
        logger.info("getBucket() called with: authentication = [" + authentication + "], session = [" + session + "]");
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            return processAuthenticated(user);
        } else if (session != null) {
            return processGuest(session);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/d")
    public ResponseEntity<?> getBucketForDebug() {
        logger.info("BucketController.getBucketForDebug");
        return new ResponseEntity<>(cachedBucketForDebug, headers, HttpStatus.OK);
    }

    private ResponseEntity<?> processGuest(HttpSession session) {
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

    private ResponseEntity<?> processAuthenticated(User user) {
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
