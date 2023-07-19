package com.example.it32007telegram.services;

import com.example.it32007telegram.models.entities.users.User;

public interface UserService {
    User getOrCreateUser(org.telegram.telegrambots.meta.api.objects.User userTg);

    User createUser(org.telegram.telegrambots.meta.api.objects.User user);

    void saveUser(User user);
}
