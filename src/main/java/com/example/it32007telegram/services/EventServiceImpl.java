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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl extends BaseServiceImpl<Event, Long, EventRepository> implements EventService{
    private final EventRepository eventRepository;

    @Override
    public SendMessage createEvent(Category.RoleCode role, Message message) {
        return null;
    }

    @Override
    public Map<String, List<Event>> getUserEvents(User user) {
        List<Event> eventList = eventRepository.findByCreatedUser_Id(user.getId());
        List<Event> myEvents = eventList.stream()
                .filter(Event::getIsMine)
                .collect(Collectors.toList());
        eventList.removeAll(myEvents);
        Map<String, List<Event>> eventMap = new HashMap<>();
        eventMap.put("created", myEvents);
        eventMap.put("enrolled", eventList);
        return eventMap;
    }

    @Override
    public Event getEventByNameAndUser(Optional<User> userOptional) {
        return null;
    }
}
