package com.example.partypal.daos.repositories;
import com.example.partypal.daos.repositories.base.BaseRepository;
import com.example.partypal.models.entities.telegram.State;
import org.springframework.stereotype.Repository;

@Repository
public interface StateRepository extends BaseRepository<State, Long> {
    State findByCode(String code);
}
