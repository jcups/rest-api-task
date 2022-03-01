package ru.jcups.restapitask.repository;

import org.springframework.data.repository.CrudRepository;
import ru.jcups.restapitask.model.Item;

public interface ItemRepository extends CrudRepository<Item, Long> {
}
