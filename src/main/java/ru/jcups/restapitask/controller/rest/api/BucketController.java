package ru.jcups.restapitask.controller.rest.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.jcups.restapitask.model.Bucket;
import ru.jcups.restapitask.model.Role;
import ru.jcups.restapitask.service.BucketService;
import ru.jcups.restapitask.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/buckets")
public class BucketController {

	private final BucketService bucketService;
	private final UserService userService;

	@GetMapping("/{bucketId}")
	public ResponseEntity<Bucket> getBucket(@PathVariable Long bucketId, @RequestHeader("X-App-Token") String token) {
		if (hasAccess(token)) {
			return new ResponseEntity<>(bucketService.getById(bucketId), HttpStatus.OK);
		} else
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}

	@DeleteMapping("/{bucketId}/items/{itemId}")
	public ResponseEntity<?> deleteFromBucket(@PathVariable Long bucketId, @PathVariable Long itemId,
											  @RequestHeader("X-App-Token") String token) {
		if (hasAccess(token)) {
			Bucket bucket = bucketService.getById(bucketId);
			bucket.deleteItem(itemId);
			bucketService.update(bucket);
			return new ResponseEntity<>(HttpStatus.OK);
		} else
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}

	@PatchMapping("/{bucketId}/items/{itemId}")
	public ResponseEntity<?> addToBucket(@PathVariable Long bucketId, @PathVariable Long itemId,
										 @RequestParam(required = false, defaultValue = "1") int quantity,
										 @RequestHeader("X-App-Token") String token) {
		if (hasAccess(token)) {
			bucketService.addItem(bucketId, itemId, quantity);
			return new ResponseEntity<>(HttpStatus.OK);
		} else
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}

	private boolean hasAccess(String token) {
		return userService.findUserByToken(token).getMaxPriorityRole().ordinal() >= Role.ROLE_MODERATOR.ordinal();
	}
}
