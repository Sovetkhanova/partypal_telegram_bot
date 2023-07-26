package com.example.partypal.daos.repositories;

import com.example.partypal.daos.repositories.base.BaseEntityRepository;
import com.example.partypal.models.entities.base.Country;
import org.springframework.stereotype.Repository;


@Repository
public interface CountryRepository extends BaseEntityRepository<Country> {

}