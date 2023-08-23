package com.example.partypal.daos.repositories;

import com.example.partypal.daos.repositories.base.BaseEntityRepository;
import com.example.partypal.models.entities.TimeZone;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeZoneRepository extends BaseEntityRepository<TimeZone> {
}
