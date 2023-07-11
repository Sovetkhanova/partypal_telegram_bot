package com.example.it32007telegram.daos.repositories;

import com.example.it32007telegram.models.entities.users.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStatusRepository extends JpaRepository<UserStatus, Long> {

    UserStatus findByCode(String name);
}