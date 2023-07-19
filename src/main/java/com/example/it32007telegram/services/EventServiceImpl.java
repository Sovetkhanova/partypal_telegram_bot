package com.example.it32007telegram.services;

import com.example.it32007telegram.daos.repositories.EventRepository;
import com.example.it32007telegram.models.entities.Event;
import com.example.it32007telegram.models.entities.base.Category;
import com.example.it32007telegram.models.entities.users.User;
import com.example.it32007telegram.services.base.BaseServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl extends BaseServiceImpl<Event, Long, EventRepository> implements EventService{
    private final EventRepository eventRepository;

    @Override
    public SendMessage createEvent(Category.Code role, Message message) {
        return null;
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

    @Override
    public Event getEventByNameAndUser(Optional<User> userOptional) {
        return null;
    }
}
