package ru.jcups.restapitask.service.impl;

import org.springframework.stereotype.Service;
import ru.jcups.restapitask.model.Bucket;
import ru.jcups.restapitask.model.Item;
import ru.jcups.restapitask.repository.BucketRepository;
import ru.jcups.restapitask.repository.ItemRepository;
import ru.jcups.restapitask.service.BucketService;

@Service
public class DefaultBucketService extends DefaultCrudService<Bucket> implements BucketService {

    private final BucketRepository bucketRepository;
    private final ItemRepository itemRepository;

    public DefaultBucketService(BucketRepository bucketRepository, ItemRepository itemRepository) {
        super(bucketRepository);
        this.bucketRepository = bucketRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public void addItem(Long bucketId, Long itemId, int quantity) {
        Bucket bucket = bucketRepository.findById(bucketId).get();
        Item item = itemRepository.findById(itemId).get();
        bucket.addItem(item, quantity);
        bucketRepository.save(bucket);
    }
}
