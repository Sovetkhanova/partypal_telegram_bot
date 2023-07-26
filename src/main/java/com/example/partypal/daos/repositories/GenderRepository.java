package com.example.partypal.daos.repositories;

import com.example.partypal.daos.repositories.base.BaseEntityRepository;
import com.example.partypal.models.entities.users.Gender;
import org.springframework.stereotype.Repository;

@Repository
public interface GenderRepository extends BaseEntityRepository<Gender> {

}