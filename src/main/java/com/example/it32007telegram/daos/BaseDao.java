package com.example.it32007telegram.daos;

import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BaseDao<T> {
    T find(Long id, Class<T> clazz);

    Optional<T> findOptional(Long id, Class<T> clazz);

    Optional<T> findOptional(String code, Class<T> clazz);

    T find(String code, Class<T> clazz);

    Page<T> findAll(int page, int count, String sort, Class<T> clazz);

    Long total(Class<T> clazz);

    Collection<T> findAll(Class<T> clazz);

    T save(T data);

    void delete(T data);

    Boolean existsById(Long id, Class<T> clazz);

    List<T> saveAll(List<T> collection, Class<T> clazz);

}
