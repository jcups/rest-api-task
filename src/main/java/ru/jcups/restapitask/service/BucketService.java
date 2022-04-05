package ru.jcups.restapitask.service;

import ru.jcups.restapitask.model.Bucket;

public interface BucketService extends CrudService<Bucket> {

	void addItem(Long bucketId, Long itemId, int quantity);

}
