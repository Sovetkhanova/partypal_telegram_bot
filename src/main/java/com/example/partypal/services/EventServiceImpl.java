package com.example.partypal.services;

import com.example.partypal.daos.repositories.CountryRepository;
import com.example.partypal.daos.repositories.EventRepository;
import com.example.partypal.daos.repositories.UserEventLinkRepository;
import com.example.partypal.models.entities.Event;
import com.example.partypal.models.entities.users.User;
import com.example.partypal.services.base.BaseServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class EventServiceImpl extends BaseServiceImpl<Event, Long, EventRepository> implements EventService{
    private final EventRepository eventRepository;
    private final CountryRepository countryRepository;
    private final UserEventLinkRepository userEventLinkRepository;
    private final UserService userService;

    @Override
    @Transactional
    public Event createEvent(User user, Long messageId) {
        Event event = Event.builder()
                .createdUser(user)
                .tgId(messageId)
                .country(countryRepository.findById(1L).orElse(null))
                .build();
        Event eventSaved =  eventRepository.saveAndFlush(event);
        user.setActualEvent(eventSaved);
        userService.saveUser(user);
        return eventSaved;
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
    @Transactional
    public void deleteAll(Collection<Event> events) {
        eventRepository.deleteAll(events);
    }

    @Override
    public Map<String, List<Event>> getUserEvents(User user) {
        List<Event> mineEvents = eventRepository.findByCreatedUser_IdAndDetectedLanguageIsNotNull(user.getId());
        List<Event> enrolledEvents = new ArrayList<>();
        userEventLinkRepository.findAllByUser_Id(user.getId()).forEach(userEventLink -> enrolledEvents.add(userEventLink.getEvent()));
        Map<String, List<Event>> eventMap = new HashMap<>();
        if(mineEvents.isEmpty() && enrolledEvents.isEmpty()){
            return null;
        }
        eventMap.put("created", mineEvents);
        eventMap.put("enrolled", enrolledEvents);
        return eventMap;
    }
}
