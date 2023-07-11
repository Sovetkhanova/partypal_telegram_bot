package com.example.it32007telegram.daos;

import com.example.it32007telegram.models.entities.users.User;

import java.util.Optional;

public interface UserDao extends CriteriaDao<User> {

    User save(User user);

    User findById(Long id);

    Optional<User> findByUsername(String username);

    User createUser(org.telegram.telegrambots.meta.api.objects.User user);
}
