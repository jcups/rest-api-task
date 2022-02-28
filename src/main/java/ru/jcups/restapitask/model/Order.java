package ru.jcups.restapitask.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id == order.id && Objects.equals(createdDate, order.createdDate)
                && Objects.equals(updateDate, order.updateDate) && Objects.equals(user, order.user)
                && Objects.equals(sum, order.sum) && status == order.status && Objects.equals(items, order.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user);
    }

    @Override
    public String toString() {
        return "\nOrder{" +
                "\n\tid=" + id +
                ", \n\tcreatedDate=" + createdDate +
                ", \n\tupdateDate=" + updateDate +
                ", \n\tuser=" + user +
                ", \n\tsum=" + sum +
                ", \n\tstatus=" + status +
                ", \n\titems=" + items +
                '}';
    }


}
