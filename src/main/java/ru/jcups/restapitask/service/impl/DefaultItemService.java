package ru.jcups.restapitask.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(DefaultItemService.class);

    private final ItemRepository itemRepository;
    private final UserService userService;

    public DefaultItemService(ItemRepository itemRepository, UserService userService) {
        super(itemRepository);
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public void addToBucket(long id, User user, int quantity) {
        logger.info("DefaultItemService.addToBucket");
        logger.info("addToBucket() called with: id = [" + id + "], user = [" + user + "], quantity = [" + quantity + "]");
        assert id != 0L;
        assert user != null;
        user = userService.getById(user.getId());
        Bucket bucket = user.getBucket();
        bucket.addItem(this.getById(id), quantity);
        user.setBucket(bucket);
        userService.update(user);
    }


    @Override
    public List<Item> getItemsLimitFour() {
        logger.info("DefaultItemService.getItemsLimitFour");
        logger.info("getItemsLimitFour() called");
        List<Item> items = this.getAll();
        if (items.size() > 4) {
            List<Item> list = items.stream().limit(4).collect(Collectors.toList());
            logger.debug("DefaultItemService.getItemsLimitFour() returned: " + list);
            return list;
        }
        logger.debug("DefaultItemService.getItemsLimitFour() returned: " + items);
        return items;
    }

}
