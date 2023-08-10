package com.example.partypal.services;

import com.example.partypal.daos.repositories.DocumentRepository;
import com.example.partypal.models.entities.telegram.Document;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DocumentServiceImpl {
    private final DocumentRepository documentRepository;

    @Transactional
    public List<Document> saveAll(List<Document> documentList){
        return documentRepository.saveAll(documentList);
    }
}
