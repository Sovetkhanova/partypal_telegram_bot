package com.example.it32007telegram.daos.repositories;

import com.example.it32007telegram.daos.repositories.base.BaseRepository;
import com.example.it32007telegram.models.entities.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends BaseRepository<Event, Long> {
    Optional<Event> findByTgId(Long id);

    List<Event> findByCreatedUser_Id(Long id);
}
