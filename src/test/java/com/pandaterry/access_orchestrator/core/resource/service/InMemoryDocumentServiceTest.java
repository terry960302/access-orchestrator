package com.pandaterry.access_orchestrator.core.resource.service;

import com.pandaterry.access_orchestrator.core.resource.DocumentId;
import com.pandaterry.access_orchestrator.core.resource.SubjectId;
import com.pandaterry.access_orchestrator.core.resource.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryDocumentServiceTest {
    private InMemoryDocumentService service;

    @BeforeEach
    void setUp() {
        service = new InMemoryDocumentService();
    }

    @Test
    @DisplayName("문서를 저장하면 조회할 수 있어야 한다")
    void createAndGet_ShouldReturnDocument() {
        Document document = Document.builder()
                .id(new DocumentId("doc1"))
                .title("title")
                .content("content")
                .authorId(new SubjectId("author"))
                .state("NEW")
                .metadata(Map.of())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        service.create(document);

        Document result = service.get(new DocumentId("doc1"));
        assertThat(result).isEqualTo(document);
    }

    @Test
    @DisplayName("문서를 수정하면 변경 내용이 반영되어야 한다")
    void update_ShouldReplaceDocument() {
        Document document = Document.builder().id(new DocumentId("doc1")).build();
        service.create(document);

        Document updated = Document.builder().id(new DocumentId("doc1")).title("new").build();
        service.update(new DocumentId("doc1"), updated);

        assertThat(service.get(new DocumentId("doc1")).getTitle()).isEqualTo("new");
    }

    @Test
    @DisplayName("문서를 삭제하면 조회되지 않아야 한다")
    void delete_ShouldRemoveDocument() {
        Document document = Document.builder().id(new DocumentId("doc1")).build();
        service.create(document);

        service.delete(new DocumentId("doc1"));

        assertThat(service.get(new DocumentId("doc1"))).isNull();
    }
}
