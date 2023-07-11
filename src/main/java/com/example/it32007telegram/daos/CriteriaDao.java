package com.example.it32007telegram.daos;

import com.example.it32007telegram.exceptions.NotFoundException;
import com.example.it32007telegram.models.dtos.SearchParameters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.Subgraph;
import javax.persistence.criteria.*;
import java.util.*;

public interface CriteriaDao<T> {

    default <K, X> Join<K, X> getOrCreateJoin(From<?, K> from, String attribute) {
        return getOrCreateJoin(from, attribute, JoinType.INNER);

    }

    default <K, X> Join<K, X> getOrCreateJoin(From<?, K> from, String attribute, JoinType joinType) {
        Optional<Join<K, ?>> optionalJoin = from.getJoins().stream()
                .filter(join -> join.getAttribute().getName().equals(attribute) && join.getJoinType().equals(joinType)).findAny();

        if (optionalJoin.isPresent()) {
            return optionalJoin.map(join -> (Join<K, X>) join).get();
        } else {
            Optional<Fetch<K, ?>> optionalFetchJoin = from.getFetches().stream()
                    .filter(join -> join.getAttribute().getName().equals(attribute) && join.getJoinType().equals(joinType)).findAny();

            if (optionalFetchJoin.isPresent()) {
                return optionalFetchJoin.map(join -> (Join<K, X>) join).get();
            } else {
                return from.join(attribute, joinType);
            }
        }
    }

    default void addGraph(EntityGraph fetchGraph, List<String> attributePaths) {
        final Map<String, Subgraph> attributeNameGraphPair = new HashMap<>();

        attributePaths.forEach(attributePath -> {
            String[] attributeNames = attributePath.split("\\.");
            String path = attributePath.contains(".") ? attributeNames[0] : attributePath;
            Subgraph subgraph = attributeNameGraphPair.computeIfAbsent(path, fetchGraph::addSubgraph);

            for (int i = 1; i < attributeNames.length; i++) {
                String attribute = attributeNames[i];
                path = path.concat(".").concat(attribute);
                Subgraph existingGraph = attributeNameGraphPair.get(path);

                if (existingGraph == null) {
                    subgraph = subgraph.addSubgraph(attribute);
                    attributeNameGraphPair.put(path, subgraph);
                } else {
                    subgraph = existingGraph;
                }
            }

        });
    }

    @Transactional
    default Page<T> findPaginated(SearchParameters parameters, String... attributePaths) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> userCriteriaQuery = criteriaBuilder.createQuery(getType());
        Root<T> from = userCriteriaQuery.from(getType());
        Predicate wherePredicate = parameters.buildWhere(this.getImplementation(), criteriaBuilder, from, userCriteriaQuery);

        userCriteriaQuery.where(wherePredicate);
        List<T> result;
        if (attributePaths.length != 0) {
            EntityGraph<T> fetchGraph = entityManager.createEntityGraph(getType());
            this.addGraph(fetchGraph, Arrays.asList(attributePaths));
            result = entityManager.createQuery(userCriteriaQuery)
                    .setHint("javax.persistence.loadgraph", fetchGraph)
                    .setFirstResult(parameters.getPage() * parameters.getCount())
                    .setMaxResults(parameters.getCount())
                    .getResultList();
        } else {
            result = entityManager.createQuery(userCriteriaQuery)
                    .setFirstResult(parameters.getPage() * parameters.getCount())
                    .setMaxResults(parameters.getCount())
                    .getResultList();
        }


        return new PageImpl<T>(result, PageRequest.of(parameters.getPage(), parameters.getCount()), count(parameters));
    }

    @Transactional
    default List<T> findAll(SearchParameters parameters, String... attributePaths) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> userCriteriaQuery = criteriaBuilder.createQuery(getType());
        Root<T> from = userCriteriaQuery.from(getType());

        Predicate wherePredicate = parameters.buildWhere(this.getImplementation(), criteriaBuilder, from, userCriteriaQuery);

        userCriteriaQuery.where(wherePredicate);

        if (attributePaths.length != 0) {
            EntityGraph<T> fetchGraph = entityManager.createEntityGraph(getType());
            this.addGraph(fetchGraph, Arrays.asList(attributePaths));
            return entityManager.createQuery(userCriteriaQuery)
                    .setHint("javax.persistence.loadgraph", fetchGraph)
                    .getResultList();
        } else {
            return entityManager.createQuery(userCriteriaQuery)
                    .getResultList();
        }

    }

    @Transactional
    default Optional<T> findOneOptional(SearchParameters parameters, String... attributePaths) {
        return findAll(parameters, attributePaths).stream().findAny();

    }

    @Transactional
    default T findOne(SearchParameters parameters, String... attributePaths) {
        return findOneOptional(parameters, attributePaths).orElseThrow(() -> new NotFoundException(String.format("%s was not found !", getType().getSimpleName())));
    }


    @Transactional
    default Long count(SearchParameters parameters) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<T> countRoot = countQuery.from(getType());
        Predicate countPredicate = parameters.buildWhere(this.getImplementation(), criteriaBuilder, countRoot, countQuery);
        countQuery.select(criteriaBuilder.count(countRoot));
        countQuery.where(countPredicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }

    EntityManager getEntityManager();

    Class<T> getType();

    CriteriaDao<T> getImplementation();

    @Transactional
    default Optional<T> findByIdOptional(Long id, String... attributePaths) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(getType());
        Root<T> from = criteriaQuery.from(getType());
        criteriaQuery.where(criteriaBuilder.equal(from.get("id"), id));
        if (attributePaths.length != 0) {
            EntityGraph<T> fetchGraph = entityManager.createEntityGraph(getType());
            this.addGraph(fetchGraph, Arrays.asList(attributePaths));
            return entityManager.createQuery(criteriaQuery)
                    .setHint("javax.persistence.loadgraph", fetchGraph)
                    .getResultStream().findAny();
        } else {
            return entityManager.createQuery(criteriaQuery)
                    .getResultStream().findAny();
        }
    }

    @Transactional
    default T findById(Long id, String... attributePaths) {
        return findByIdOptional(id, attributePaths).orElseThrow(() -> new NotFoundException(String.format("%s was not found !", getType().getSimpleName())));
    }

}
