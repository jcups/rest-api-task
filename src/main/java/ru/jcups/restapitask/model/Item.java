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

    float rate;

    long countRates;

    long views;

    @ElementCollection(fetch = FetchType.EAGER)
    Set<String> allImagesUrls;

    @ElementCollection(fetch = FetchType.EAGER,targetClass = LinkedHashMap.class)
    Map<String, Map<String, String>> params;

    public void processRate(float stars) {
        if (stars >= 0 && stars <= 5) {
            this.rate = (rate * countRates + stars) / ++countRates;
        }
    }

    public void plusView() {
        views++;
    }

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
        if (item.views != 0)
            this.views = item.views;
        if (item.rate != 0)
            this.rate = item.rate;
        if (item.countRates != 0)
            this.countRates = item.countRates;
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
        return "\nItem{" +
                "\n\tid=" + id +
                ", \n\ttitle='" + title + '\'' +
                ", \n\tprice=" + price +
                ", \n\tdescription='" + description + '\'' +
                '}';
    }
}
