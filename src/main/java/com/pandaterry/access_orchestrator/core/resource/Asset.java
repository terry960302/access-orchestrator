package com.pandaterry.access_orchestrator.core.resource;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class Asset {
    private final String id;
    private final String name;
    private final String type;
    private final String url;
    private final String ownerId;
    private final String state;
    private final Map<String, Object> metadata;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}