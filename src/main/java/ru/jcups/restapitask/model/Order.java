package ru.jcups.restapitask.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order extends DefaultEntity {

	@CreationTimestamp
	LocalDate createdDate;

	@UpdateTimestamp
	LocalDate updateDate;

	@ManyToOne
	User user;

	BigDecimal sum;

	@Enumerated(EnumType.STRING)
	OrderStatus status;

	@ManyToMany
	@JoinTable(name = "orders_items", joinColumns = @JoinColumn(name = "order_id"),
			inverseJoinColumns = @JoinColumn(name = "item_id"))
	List<Item> items;

	@Override
	public void refresh(DefaultEntity entity) {
		Order order = (Order) entity;
		if (order.updateDate != null)
			this.updateDate = order.updateDate;
		if (order.sum != null)
			this.sum = order.sum;
		if (order.status != null)
			this.status = order.status;
		if (order.items != null)
			this.items = order.items;
	}

	@EqualsAndHashCode(callSuper = true)
	@Entity
	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@Table(name = "order_parts")
	public static class OrderPart extends DefaultEntity {

		BigDecimal amount;

		BigDecimal sum;

		@ManyToOne
		@JoinColumn(name = "item_id")
		Item item;

		@ManyToOne
		@JoinColumn(name = "order_id")
		Order order;

		public OrderPart(Order order, Item item, long amount) {
			this.order = order;
			this.item = item;
			this.amount = new BigDecimal(amount);
			this.sum = new BigDecimal(item.getPrice().longValue() * amount);
		}

		@Override
		public void refresh(DefaultEntity entity) {
			OrderPart part = (OrderPart) entity;
			if (part.amount != null)
				this.amount = part.amount;
			if (part.sum != null)
				this.sum = part.sum;
		}
	}

	public enum OrderStatus {
		NEW, APPROVED, CANCELED, PAID, CLOSED
	}

}
