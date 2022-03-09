package ru.jcups.restapitask.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bucket extends DefaultEntity {

    @OneToOne
    User user;

    @ManyToMany
    @JoinTable(name = "buckets_items", joinColumns = @JoinColumn(name = "bucket_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    List<Item> items;

    public void addItem(Item item, int quantity) {
        if (items == null) {
            items = new LinkedList<>();
        }
        for (int i = 0; i < quantity; i++)
            items.add(item);
    }

    @Override
    public void refresh(DefaultEntity entity) {
        Bucket bucket = (Bucket) entity;
        if (bucket.user != null)
            this.user = bucket.user;
        if (bucket.items != null)
            this.items = bucket.items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bucket bucket = (Bucket) o;
        return id == bucket.getId() && Objects.equals(user, bucket.user) && Objects.equals(items, bucket.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, items);
    }

    @Override
    public String toString() {
        return "Bucket{" +
                "id=" + id +
                ", user=" + user +
                ", items=" + items +
                '}';
    }
}
