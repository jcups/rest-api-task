package ru.jcups.restapitask.service.impl;

import org.springframework.stereotype.Service;
import ru.jcups.restapitask.model.Bucket;
import ru.jcups.restapitask.repository.BucketRepository;
import ru.jcups.restapitask.service.BucketService;

@Service
public class DefaultBucketService extends DefaultCrudService<Bucket> implements BucketService {

    private final BucketRepository bucketRepository;

    public DefaultBucketService(BucketRepository bucketRepository) {
        super(bucketRepository);
        this.bucketRepository = bucketRepository;
    }
}
