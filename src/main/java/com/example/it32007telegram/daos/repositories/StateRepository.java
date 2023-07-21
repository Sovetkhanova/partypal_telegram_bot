package com.example.it32007telegram.daos.repositories;
import com.example.it32007telegram.daos.repositories.base.BaseRepository;
import com.example.it32007telegram.models.entities.telegram.State;
import org.springframework.stereotype.Repository;

@Repository
public interface StateRepository extends BaseRepository<State, Long> {
    State findByCode(String code);
}
