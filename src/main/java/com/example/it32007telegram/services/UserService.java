package com.example.it32007telegram.services;

import com.example.it32007telegram.models.entities.users.User;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface UserService {
    User createUser(Message message);
}
