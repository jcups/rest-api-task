package ru.jcups.restapitask.service.impl;

import org.hibernate.ObjectNotFoundException;
import org.springframework.data.repository.CrudRepository;
import ru.jcups.restapitask.model.DefaultEntity;
import ru.jcups.restapitask.service.CrudService;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

public abstract class DefaultCrudService<T extends DefaultEntity> implements CrudService<T> {

    private final CrudRepository<T, Long> repository;
    protected Class<? extends DefaultEntity> clazz;
    protected String clazzName;

    protected DefaultCrudService(CrudRepository<T, Long> repository) {
        this.repository = repository;
        this.init();
    }

    @Override
    public T create(T t) {
        System.out.println("DefaultCrudService.create");
        System.out.println("t = " + t);
        return repository.save(t);
    }

    @Override
    public T update(T t) {
        System.out.println("DefaultCrudService.update");
        System.out.println("t = " + t);
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
        System.out.println("DefaultCrudService.getAll");
        List<T> result = (List<T>) repository.findAll();
        if (result.isEmpty())
            throw new ObjectNotFoundException(clazz, clazzName);
        return result;
    }

    @Override
    public T getById(long id) {
        System.out.println("DefaultCrudService.getById");
        System.out.println("id = " + id);
        Optional<T> found = repository.findById(id);
        if (found.isPresent())
            return found.get();
        else
            throw new ObjectNotFoundException(clazz, clazzName);
    }

    @Override
    public boolean delete(T t) {
        System.out.println("DefaultCrudService.delete");
        System.out.println("t = " + t);
        if (repository.existsById(t.getId()))
            repository.delete(t);
        else
            throw new ObjectNotFoundException(clazz, clazzName);
        return !repository.existsById(t.getId());
    }

    @Override
    public boolean deleteById(long id) {
        System.out.println("DefaultCrudService.deleteById");
        System.out.println("id = " + id);
        if (repository.existsById(id))
            repository.deleteById(id);
        else
            throw new ObjectNotFoundException(clazz, clazzName);
        return !repository.existsById(id);
    }

    private void init() {
        Class<?> actualClass = this.getClass();
        ParameterizedType type = (ParameterizedType) actualClass.getGenericSuperclass();
        clazz = (Class<T>) type.getActualTypeArguments()[0];
        clazzName = clazz.getSimpleName();
    }
}

