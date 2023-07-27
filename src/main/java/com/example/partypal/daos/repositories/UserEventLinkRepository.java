package com.example.partypal.daos.repositories;

import com.example.partypal.daos.repositories.base.BaseEntityRepository;
import com.example.partypal.models.entities.UserEventLink;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserEventLinkRepository extends BaseEntityRepository<UserEventLink> {
    List<UserEventLink> findAllByUser_Id(Long userId);

    boolean existsByUser_IdAndEvent_Id(Long userId, Long eventId);

    void deleteAllByUser_IdAndEvent_Id(Long userId, Long eventId);
}