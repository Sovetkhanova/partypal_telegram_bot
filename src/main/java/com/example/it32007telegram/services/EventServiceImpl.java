package com.example.it32007telegram.services;

import com.example.it32007telegram.daos.repositories.EventRepository;
import com.example.it32007telegram.models.entities.Event;
import com.example.it32007telegram.models.entities.users.User;
import com.example.it32007telegram.services.base.BaseServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl extends BaseServiceImpl<Event, Long, EventRepository> implements EventService{
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public Event createEvent(User user, Long messageId) {
        Event event = Event.builder()
                .createdUser(user)
                .tgId(messageId)
                .build();
        return eventRepository.saveAndFlush(event);
    }

    @Override
    public Event getOrCreateEvent(User user, Long messageId) {
        return findEventByMessageId(messageId).orElseGet(() -> createEvent(user, messageId));
    }

    @Cacheable("event")
    public Optional<Event> findEventByMessageId(Long messageId){
        return eventRepository.findByTgId(messageId);
    }

    @Override
    public Map<String, List<Event>> getUserEvents(User user) {
        List<Event> events = eventRepository.findAll();
        List<Event> mineEvents = events.stream().filter(event -> (event.getCreatedUser() != null) && event.getCreatedUser().getId().equals(user.getId())).collect(Collectors.toList());
        events.removeAll(mineEvents);
        Map<String, List<Event>> eventMap = new HashMap<>();
        eventMap.put("created", mineEvents);
        eventMap.put("enrolled", events);
        return eventMap;
    }
}
