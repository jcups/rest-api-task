package ru.jcups.restapitask.service;

import ru.jcups.restapitask.model.DefaultEntity;

import java.util.List;

public interface CrudService<T extends DefaultEntity> {

    T create(T t);

    T update(T obj);

    List<T> getAll();

    T getById(long id);

    boolean delete(T t);

    boolean deleteById(long id);

}
