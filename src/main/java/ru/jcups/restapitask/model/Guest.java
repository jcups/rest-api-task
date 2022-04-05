package ru.jcups.restapitask.model;

import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Data
@Component
@Scope("session")
public class Guest {

	Bucket bucket;

	public Guest() {
		this.bucket = new Bucket();
	}

	public void addItemToBucket(Item item, int quantity) {
		bucket.addItem(item, quantity);
	}
}
