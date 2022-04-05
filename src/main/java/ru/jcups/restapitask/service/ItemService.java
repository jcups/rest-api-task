package ru.jcups.restapitask.service;

import org.springframework.data.domain.Page;
import ru.jcups.restapitask.model.Item;

public interface ItemService extends CrudService<Item> {

    Page<Item> findAllAtPage(int page, int quantity);
}
