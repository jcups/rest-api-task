package ru.jcups.restapitask.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "order_parts")
public class OrderPart extends DefaultEntity {

    BigDecimal amount;

    BigDecimal sum;

    @ManyToOne
    @JoinColumn(name = "item_id")
    Item item;

    @ManyToOne
    @JoinColumn(name = "order_id")
    Order order;

    public OrderPart (Order order, Item item, long amount) {
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
