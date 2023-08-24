package com.example.partypal.daos.repositories;

import com.example.partypal.daos.repositories.base.BaseRepository;
import com.example.partypal.models.entities.SubscriptionEventLink;
import com.example.partypal.projectors.SubscriptionEventLinkProjector;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionEventLinkRepository extends BaseRepository<SubscriptionEventLink, Long>{
    @Query(value = "SELECT " +
            "s.promoteUntil as promoteUntil, " +
            "e.name as name, " +
            "subsc.code as code " +
            "FROM SubscriptionEventLink s " +
            "LEFT JOIN Event e on (s.event.id = e.id) " +
            "LEFT JOIN Subscription subsc on (s.subscription.id = subsc.id) " +
            "WHERE (s.event.createdUser.id =:userId)")
    List<SubscriptionEventLinkProjector> findAllByEvent_CreatedUser_Id(Long userId);
}