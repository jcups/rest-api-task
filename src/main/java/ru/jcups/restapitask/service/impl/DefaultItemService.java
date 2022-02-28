package ru.jcups.restapitask.service.impl;

import org.springframework.stereotype.Service;
import ru.jcups.restapitask.model.Bucket;
import ru.jcups.restapitask.model.Item;
import ru.jcups.restapitask.model.User;
import ru.jcups.restapitask.repository.ItemRepository;
import ru.jcups.restapitask.service.ItemService;
import ru.jcups.restapitask.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultItemService extends DefaultCrudService<Item> implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    public DefaultItemService(ItemRepository itemRepository, UserService userService) {
        super(itemRepository);
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public void addToBucket(long id, User user, int quantity) {
        assert id != 0L;
        assert user != null;
        user = userService.getById(user.getId());
        Bucket bucket = user.getBucket();
        bucket.addItem(this.getById(id), quantity);
        user.setBucket(bucket);
        userService.update(user);
    }

    @Override
    public void plusView(long id) {
        Item item = this.getById(id);
        item.plusView();
        this.update(item);
    }

    @Override
    public void newRate(long id, float rate) {
        Item item = this.getById(id);
        item.processRate(rate);
        this.update(item);
    }

    @Override
    public List<Item> getFourItemsWithBiggestRate() {
        List<Item> items = this.getAllByRateBetween(0, 5);
        if (items.size() > 4) {
            List<Item> list = items.stream().sorted((o1, o2) -> Float.compare(o1.getRate(), o2.getRate()))
                    .limit(4).collect(Collectors.toList());
            return list;
        }
        return items;
    }

    @Override
    public List<Item> getAllByRateBetween(float from, float to) {
        return itemRepository.getAllByRateBetween(from, to);
    }
}
