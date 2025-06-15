package com.pandaterry.access_orchestrator.core.resource.service;

import com.pandaterry.access_orchestrator.core.resource.DocumentId;
import com.pandaterry.access_orchestrator.core.resource.Document;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
public class InMemoryDocumentService implements DocumentService {
    private final Map<DocumentId, Document> documents = new ConcurrentHashMap<>();

    @Override
    public Document create(Document document) {
        documents.put(document.getId(), document);
        return document;
    }

    @Override
    public Document get(DocumentId id) {
        return documents.get(id);
    }

    @Override
    public List<Document> getAll() {
        return List.copyOf(documents.values());
    }

    @Override
    public Document update(DocumentId id, Document document) {
        documents.put(id, document);
        return document;
    }

    @Override
    public void delete(DocumentId id) {
        documents.remove(id);
    }
}
