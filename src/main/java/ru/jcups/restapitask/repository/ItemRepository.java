package ru.jcups.restapitask.repository;

import org.springframework.data.repository.CrudRepository;
import ru.jcups.restapitask.model.Item;

import java.util.List;

public interface ItemRepository extends CrudRepository<Item, Long> {

    List<Item> getAllByRateBetween (float from, float to);
}
