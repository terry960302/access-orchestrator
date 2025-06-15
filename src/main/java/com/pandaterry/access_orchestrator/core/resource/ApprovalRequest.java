package com.pandaterry.access_orchestrator.core.resource;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class ApprovalRequest {
    private final ApprovalRequestId id;
    private final ResourceId resourceId;
    private final String resourceType;
    private final SubjectId requesterId;
    private final SubjectId approverId;
    private final String state;
    private final Map<String, Object> metadata;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}