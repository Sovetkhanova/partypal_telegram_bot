package com.example.it32007telegram.services;

import com.example.it32007telegram.models.entities.Event;
import com.example.it32007telegram.models.entities.base.Category;
import com.example.it32007telegram.models.entities.users.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EventService {
    SendMessage createEvent(Category.Code role, Message message);

    Map<String, List<Event>> getUserEvents(User user);

    Event getEventByNameAndUser(Optional<User> userOptional);
}
