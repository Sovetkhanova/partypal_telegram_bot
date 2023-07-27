package com.example.partypal.services;

import com.example.partypal.models.entities.users.User;

import java.util.Optional;

public interface UserService {
    User getOrCreateUser(org.telegram.telegrambots.meta.api.objects.User userTg);

    User createUser(org.telegram.telegrambots.meta.api.objects.User user);

    void saveUser(User user);

    Optional<User> findUserById(Long id);

    void deleteRemark(long userId, long eventId);
}
