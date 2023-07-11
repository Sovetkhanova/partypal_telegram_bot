package com.example.it32007telegram.services.base;

import com.example.it32007telegram.daos.repositories.base.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public abstract class BaseServiceImpl<T, ID extends Serializable, R extends BaseRepository<T, ID>> implements BaseService<T, ID> {

    private R repository;

    protected R getRepository() {
        return this.repository;
    }

    @Autowired
    public void setRepository(R repository) {
        this.repository = repository;
    }

    @Transactional
    public T save(T value) {
        return this.getRepository().saveAndFlush(value);
    }

    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        return getRepository().saveAll(entities);
    }

    @Transactional
    public T edit(T value) {
        return this.getRepository().saveAndFlush(value);
    }

    @Transactional
    public Optional<T> find(ID id) {
        return this.getRepository().findById(id);
    }

    public T getReference(ID id) {
        return this.getRepository().getById(id);
    }

    public boolean isExistById(ID id) {
        return this.getRepository().existsById(id);
    }

    @Transactional
    public void delete(ID id) {
        this.getRepository().deleteById(id);
    }

    @Transactional
    public void delete(T id) {
        this.getRepository().delete(id);
    }

    @Transactional
    public List<T> getAll() {
        return this.getRepository().findAll();
    }

    @Transactional
    public List<T> getAll(Sort sort) {
        return this.getRepository().findAll(sort);
    }

    @Transactional
    public List<T> getPage(int start, int size) {
        return this.getPage(start, size, (Sort) null);
    }

    @Transactional
    public List<T> getPage(int start, int size, Sort sort) {
        return this.getRepository().findAll(PageRequest.of(start, size, sort)).getContent();
    }

    @Transactional
    public long count() {
        return this.getRepository().count();
    }

    @Transactional
    public long count(Specification<T> specification) {
        return this.getRepository().count(specification);
    }

    public List<T> getAll(Specification<T> specification) {
        return this.getAll(null, specification);
    }

    public List<T> getAll(Sort sort, Specification<T> specification) {
        return this.getRepository().findAll(specification, sort);
    }

    public List<T> getPage(int start, int size, Specification<T> specification) {
        return this.getPage(start, size, null, specification);
    }

    public List<T> getPage(int start, int size, Sort sort, Specification<T> specification) {
        return this.getRepository().findAll(specification, PageRequest.of(start, size, sort)).getContent();
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return this.getRepository().findAll(pageable);
    }

    protected Pageable pageableWithSort(Pageable pageable, Sort sort) {
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }
}
