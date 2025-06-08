package com.pandaterry.access_orchestrator.core.resource;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final Map<String, Document> documents = new ConcurrentHashMap<>();

    @PostMapping
    public ResponseEntity<Document> createDocument(@RequestBody Document document) {
        documents.put(document.getId(), document);
        return ResponseEntity.ok(document);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocument(@PathVariable String id) {
        Document document = documents.get(id);
        return document != null ? ResponseEntity.ok(document) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<Document>> getAllDocuments() {
        return ResponseEntity.ok(List.copyOf(documents.values()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Document> updateDocument(@PathVariable String id, @RequestBody Document document) {
        if (!documents.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        documents.put(id, document);
        return ResponseEntity.ok(document);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String id) {
        documents.remove(id);
        return ResponseEntity.ok().build();
    }
}