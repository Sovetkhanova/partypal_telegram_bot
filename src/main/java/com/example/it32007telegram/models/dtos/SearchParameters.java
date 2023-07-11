package com.example.it32007telegram.models.dtos;

import com.example.it32007telegram.daos.CriteriaDao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Setter;
import org.springframework.data.domain.Page;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Setter
public abstract class SearchParameters<T> extends PageParameters {
    private Long organizationId;

    @JsonIgnore
    private CriteriaDao<T> criteriaDao;

    @JsonIgnore
    private String[] attributePaths;

    public abstract Predicate buildWhere(CriteriaDao<T> criteriaDao, CriteriaBuilder criteriaBuilder, Root<T> from, CriteriaQuery criteriaQuery);

    public Optional<T> findOneOptional() {
        return criteriaDao.findOneOptional(this, attributePaths);
    }

    public T findOne() {
        return criteriaDao.findOne(this, attributePaths);
    }

    public List<T> findAll() {
        return criteriaDao.findAll(this, attributePaths);
    }

    public Page<T> findPaginated() {
        return criteriaDao.findPaginated(this, attributePaths);
    }

    public Long getOrganizationId() {
        return organizationId;
    }

}
