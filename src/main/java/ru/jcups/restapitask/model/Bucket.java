package ru.jcups.restapitask.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Bucket extends DefaultEntity {

	BigDecimal sum;

	@ManyToMany
	@JoinTable(name = "buckets_items", joinColumns = @JoinColumn(name = "bucket_id"),
			inverseJoinColumns = @JoinColumn(name = "item_id"))
	List<Item> items;

	public void calcSum() {
		this.sum = BigDecimal.valueOf(this.items.stream()
				.mapToDouble(item -> item.getPrice().doubleValue()).sum());
	}

	public void addItem(Item item, int quantity) {
		if (items == null)
			this.items = new LinkedList<>();
		for (int i = 0; i < quantity; i++)
			this.items.add(item);
		this.calcSum();
	}

	public void deleteItem(Long itemId) {
		this.items = this.items.stream()
				.filter(item -> item.getId() != itemId).collect(Collectors.toList());
		this.calcSum();
	}

	@Override
	public void refresh(DefaultEntity entity) {
		Bucket bucket = (Bucket) entity;
		if (bucket.items != null)
			this.items = bucket.items;
		this.calcSum();
	}
}
