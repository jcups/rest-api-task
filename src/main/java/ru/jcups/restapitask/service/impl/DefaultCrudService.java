package ru.jcups.restapitask.service.impl;

import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.CrudRepository;
import ru.jcups.restapitask.model.DefaultEntity;
import ru.jcups.restapitask.service.CrudService;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

public abstract class DefaultCrudService<T extends DefaultEntity> implements CrudService<T> {

    private static final Logger logger = LoggerFactory.getLogger(DefaultCrudService.class);

    private final CrudRepository<T, Long> repository;
    protected Class<? extends DefaultEntity> clazz;
    protected String clazzName;

    protected DefaultCrudService(CrudRepository<T, Long> repository) {
        this.repository = repository;
        this.init();
    }

    @Override
    public T create(T t) {
        logger.info(clazzName+".create");
        logger.info("create() called with: t = [" + t + "]");
        return repository.save(t);
    }

    @Override
    public T update(T t) {
        logger.info(clazzName+".update");
        logger.info("update() called with: t = [" + t + "]");
        if (repository.existsById(t.getId())) {
            Optional<T> entity = repository.findById(t.getId());
            T found;
            if (entity.isPresent())
                found = entity.get();
            else
                throw new ObjectNotFoundException(clazz, clazzName);
            found.refresh(t);
            return repository.save(found);
        } else
            throw new ObjectNotFoundException(clazz, clazzName);
    }

    @Override
    public List<T> getAll() {
        logger.info(clazzName+".getAll");
        List<T> result = (List<T>) repository.findAll();
        if (result.isEmpty())
            throw new ObjectNotFoundException(clazz, clazzName);
        return result;
    }

    @Override
    public T getById(long id) {
        logger.info(clazzName+".getById");
        logger.info("getById() called with: id = [" + id + "]");
        Optional<T> found = repository.findById(id);
        if (found.isPresent())
            return found.get();
        else
            throw new ObjectNotFoundException(clazz, clazzName);
    }

    @Override
    public boolean delete(T t) {
        logger.info(clazzName+".delete");
        logger.info("delete() called with: t = [" + t + "]");
        if (repository.existsById(t.getId()))
            repository.delete(t);
        else
            throw new ObjectNotFoundException(clazz, clazzName);
        return !repository.existsById(t.getId());
    }

    @Override
    public boolean deleteById(long id) {
        logger.info(clazzName+".deleteById");
        logger.info("deleteById() called with: id = [" + id + "]");
        if (repository.existsById(id))
            repository.deleteById(id);
        else
            throw new ObjectNotFoundException(clazz, clazzName);
        return !repository.existsById(id);
    }

    private void init() {
        logger.info("DefaultCrudService.init");
        Class<?> actualClass = this.getClass();
        ParameterizedType type = (ParameterizedType) actualClass.getGenericSuperclass();
        clazz = (Class<T>) type.getActualTypeArguments()[0];
        clazzName = clazz.getSimpleName();
    }
}

