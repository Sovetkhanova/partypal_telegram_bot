package com.example.partypal.daos.repositories;

import com.example.partypal.daos.repositories.base.BaseRepository;
import com.example.partypal.models.entities.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends BaseRepository<Event, Long> {

    Optional<Event> findByTgId(Long id);

    List<Event> findByCreatedUser_IdAndDetectedLanguageIsNotNull(Long userId);
}
