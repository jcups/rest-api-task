package ru.jcups.restapitask.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.jcups.restapitask.model.Item;
import ru.jcups.restapitask.repository.ItemRepository;
import ru.jcups.restapitask.service.ItemService;

@Service
public class DefaultItemService extends DefaultCrudService<Item> implements ItemService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultItemService.class);

    private final ItemRepository itemRepository;

    public DefaultItemService(ItemRepository itemRepository) {
        super(itemRepository);
        this.itemRepository = itemRepository;
    }

    @Override
    public Page<Item> findAllAtPage(int page, int quantity) {
        return itemRepository.findAll(PageRequest.of(page, quantity));
    }

}
