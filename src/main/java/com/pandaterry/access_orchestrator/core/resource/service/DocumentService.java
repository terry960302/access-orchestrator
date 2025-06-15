package com.pandaterry.access_orchestrator.core.resource.service;

import com.pandaterry.access_orchestrator.core.resource.DocumentId;
import com.pandaterry.access_orchestrator.core.resource.Document;

import java.util.List;

public interface DocumentService {
    Document create(Document document);

    Document get(DocumentId id);

    List<Document> getAll();

    Document update(DocumentId id, Document document);

    void delete(DocumentId id);
}
