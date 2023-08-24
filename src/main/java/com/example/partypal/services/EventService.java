package com.example.partypal.services;

import com.example.partypal.models.entities.Event;
import com.example.partypal.models.entities.users.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
public interface EventService {
    Event createEvent(User user, Long messageId);

    Map<String, List<Event>> getUserEvents(User user);

    void saveEvent(Event event);

    void deleteAll(Collection<Event> events);
}
