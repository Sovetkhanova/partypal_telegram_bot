package com.example.it32007telegram.daos.repositories;

import com.example.it32007telegram.daos.repositories.base.BaseEntityWithCodeAndLangRepository;
import com.example.it32007telegram.models.entities.base.TimeZone;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeZoneRepository extends BaseEntityWithCodeAndLangRepository<TimeZone> {
}
