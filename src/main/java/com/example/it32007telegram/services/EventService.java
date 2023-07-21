package com.example.it32007telegram.services;

import com.example.it32007telegram.models.entities.Event;
import com.example.it32007telegram.models.entities.users.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EventService {
    Event createEvent(User user, Long messageId);

    Event getOrCreateEvent(User user, Long messageId);

    Map<String, List<Event>> getUserEvents(User user);

    Optional<Event> findEventByMessageId(Long messageId);

    void saveEvent(Event event);
}
