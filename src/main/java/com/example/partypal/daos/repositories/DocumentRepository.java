package com.example.partypal.daos.repositories;

import com.example.partypal.models.entities.telegram.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    Document findByTgId(String tgId);
}
