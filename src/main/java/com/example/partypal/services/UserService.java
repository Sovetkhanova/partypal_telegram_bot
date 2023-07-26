package com.example.partypal.services;

import com.example.partypal.models.entities.users.User;

public interface UserService {
    User getOrCreateUser(org.telegram.telegrambots.meta.api.objects.User userTg);

    User createUser(org.telegram.telegrambots.meta.api.objects.User user);

    void saveUser(User user);
}
