package com.example.it32007telegram.daos.repositories;

import com.example.it32007telegram.daos.repositories.base.BaseRepository;
import com.example.it32007telegram.models.entities.Event;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends BaseRepository<Event, Long> {

    List<Event> findAllByCity_IdAndDateAfter(Long cityId, Date date);

    Optional<Event> findByTgId(Long id);
}
