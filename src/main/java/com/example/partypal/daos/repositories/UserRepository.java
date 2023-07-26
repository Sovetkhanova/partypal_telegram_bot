package com.example.partypal.daos.repositories;

import com.example.partypal.daos.repositories.base.BaseEntityRepository;
import com.example.partypal.models.entities.users.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends BaseEntityRepository<User> {

    Optional<User> findByTelegramId(Long telegramId);
}