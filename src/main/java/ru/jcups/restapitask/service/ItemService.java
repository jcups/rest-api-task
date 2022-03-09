package ru.jcups.restapitask.service;

import org.springframework.data.domain.Page;
import ru.jcups.restapitask.model.Item;
import ru.jcups.restapitask.model.User;

import java.util.List;

public interface ItemService extends CrudService<Item> {

    void addToBucket(long id, User user, int quantity);

    List<Item> getItemsLimitFour();

    Page<Item> findAllAtPage(int page, int quantity);
}
