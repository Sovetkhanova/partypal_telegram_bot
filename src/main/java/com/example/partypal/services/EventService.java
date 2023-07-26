package com.example.partypal.services;

import com.example.partypal.models.entities.Event;
import com.example.partypal.models.entities.users.User;

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
