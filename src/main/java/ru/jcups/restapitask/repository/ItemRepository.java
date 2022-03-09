package ru.jcups.restapitask.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.jcups.restapitask.model.Item;

public interface ItemRepository extends PagingAndSortingRepository<Item, Long> {
}
