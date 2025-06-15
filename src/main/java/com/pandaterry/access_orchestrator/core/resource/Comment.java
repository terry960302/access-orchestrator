package com.pandaterry.access_orchestrator.core.resource;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class Comment {
    private final CommentId id;
    private final ResourceId resourceId;
    private final String resourceType;
    private final SubjectId authorId;
    private final String content;
    private final String visibility;
    private final Map<String, Object> metadata;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}