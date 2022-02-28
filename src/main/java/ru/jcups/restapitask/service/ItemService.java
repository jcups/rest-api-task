package ru.jcups.restapitask.service;

import ru.jcups.restapitask.model.Item;
import ru.jcups.restapitask.model.User;

import java.util.List;

public interface ItemService extends CrudService<Item> {

    void addToBucket(long id, User user, int quantity);
    void plusView(long id);
    void newRate(long id, float rate);
    List<Item> getAllByRateBetween (float from, float to);
    List<Item> getFourItemsWithBiggestRate ();
}
