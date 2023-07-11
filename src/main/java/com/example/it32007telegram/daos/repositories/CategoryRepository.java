package com.example.it32007telegram.daos.repositories;

import com.example.it32007telegram.models.entities.base.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByCode(String code);
}