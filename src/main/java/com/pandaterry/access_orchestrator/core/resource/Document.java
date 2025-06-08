package com.pandaterry.access_orchestrator.core.resource;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class Document {
    private final String id;
    private final String title;
    private final String content;
    private final String authorId;
    private final String state;
    private final Map<String, Object> metadata;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}