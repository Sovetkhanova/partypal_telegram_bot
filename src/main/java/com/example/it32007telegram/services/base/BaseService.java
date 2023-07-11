package com.example.it32007telegram.services.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface BaseService<T, ID extends Serializable> extends Serializable {

    T save(T value);

    <S extends T> List<S> saveAll(Iterable<S> entities);

    T edit(T value);

    void delete(ID id);

    void delete(T id);

    Optional<T> find(ID id);

    T getReference(ID id);

    boolean isExistById(ID id);

    List<T> getAll();

    List<T> getAll(Sort sort);

    List<T> getPage(int start, int size);

    List<T> getPage(int start, int size, Sort sort);

    Page<T> findAll(Pageable pageable);

    List<T> getAll(Specification<T> specification);

    List<T> getAll(Sort sort, Specification<T> specification);

    List<T> getPage(int start, int size, Specification<T> specification);

    List<T> getPage(int start, int size, Sort sort, Specification<T> specification);

    long count();

    long count(Specification<T> spec);
}
