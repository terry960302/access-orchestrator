package com.pandaterry.access_orchestrator.infrastructure.web;

import com.pandaterry.access_orchestrator.core.resource.Document;
import com.pandaterry.access_orchestrator.core.resource.DocumentId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pandaterry.access_orchestrator.core.resource.service.DocumentService;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<Document> createDocument(@RequestBody Document document) {
        return ResponseEntity.ok(documentService.create(document));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocument(@PathVariable String id) {
        Document document = documentService.get(new DocumentId(id));
        return document != null ? ResponseEntity.ok(document) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<Document>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Document> updateDocument(@PathVariable String id, @RequestBody Document document) {
        DocumentId documentId = new DocumentId(id);
        if (documentService.get(documentId) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(documentService.update(documentId, document));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String id) {
        documentService.delete(new DocumentId(id));
        return ResponseEntity.ok().build();
    }
}