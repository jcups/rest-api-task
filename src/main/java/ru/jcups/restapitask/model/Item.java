package ru.jcups.restapitask.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "items")
public class Item extends DefaultEntity {
    // TODO: 27.02.2022 Сделать кэширование в файл (просто так хочу)
    @Size(min = 3, max = 64, message = "Name length should be between 3 and 64 chars")
    @Column(nullable = false)
    String title;

    String brand;

    String model;

    String series;

    String titleImageUrl;

    @Positive(message = "Price cannot be negative")
    BigDecimal price;

    @Size(min = 16, max = 1024, message = "Description length should be between 16 and 1024 chars")
    @Column(nullable = false)
    String description;


    @ElementCollection(fetch = FetchType.EAGER)
    Set<String> allImagesUrls;

    @ElementCollection(fetch = FetchType.EAGER,targetClass = LinkedHashMap.class)
    Map<String, Map<String, String>> params;

    @Override
    public void refresh(DefaultEntity entity) {
        Item item = (Item) entity;
        if (item.title != null)
            this.title = item.title;
        if (item.titleImageUrl != null)
            this.titleImageUrl = item.titleImageUrl;
        if (item.price != null)
            this.price = item.price;
        if (item.description != null)
            this.description = item.description;
        if (item.allImagesUrls != null)
            this.allImagesUrls = item.allImagesUrls;
        if (item.params != null)
            this.params = item.params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == item.getId() && title.equals(item.title)
                && Objects.equals(price, item.price) && description.equals(item.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, price, description);
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", description='" + description.replaceAll("\n", "\\n") + '\'' +
                '}';
    }
}
