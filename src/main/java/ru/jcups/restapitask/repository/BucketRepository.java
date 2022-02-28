package ru.jcups.restapitask.repository;

import org.springframework.data.repository.CrudRepository;
import ru.jcups.restapitask.model.Bucket;

public interface BucketRepository extends CrudRepository<Bucket, Long> {
}
