package com.example.partypal.services;

import com.example.partypal.daos.repositories.CountryRepository;
import com.example.partypal.daos.repositories.EventRepository;
import com.example.partypal.models.entities.Event;
import com.example.partypal.models.entities.users.User;
import com.example.partypal.services.base.BaseServiceImpl;
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
    private final CountryRepository countryRepository;

    @Override
    @Transactional
    public Event createEvent(User user, Long messageId) {
        Event event = Event.builder()
                .createdUser(user)
                .tgId(messageId)
                .country(countryRepository.findById(1L).orElse(null))
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
    @Transactional
    public void saveEvent(Event event) {
        eventRepository.save(event);
    }

    @Override
    public Map<String, List<Event>> getUserEvents(User user) {
        List<Event> events = eventRepository.findAll()
                .stream()
                .filter(event -> event.getName() != null && event.getDate() != null)
                .collect(Collectors.toList());
        List<Event> mineEvents = events.stream().filter(
                event -> (event.getCreatedUser() != null) && event.getCreatedUser().getId().equals(user.getId()))
                .collect(Collectors.toList());
        events.removeAll(mineEvents);
        Map<String, List<Event>> eventMap = new HashMap<>();
        eventMap.put("created", mineEvents);
        eventMap.put("enrolled", events);
        return eventMap;
    }
}
