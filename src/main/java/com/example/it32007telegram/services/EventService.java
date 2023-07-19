package com.example.it32007telegram.services;

import com.example.it32007telegram.models.entities.Event;
import com.example.it32007telegram.models.entities.users.User;

import java.util.List;
import java.util.Map;

public interface EventService {
    Event createEvent(User user, Long messageId);

    Event getOrCreateEvent(User user, Long messageId);

    Map<String, List<Event>> getUserEvents(User user);
}
