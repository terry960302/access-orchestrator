package com.pandaterry.access_orchestrator.core.asset;

import lombok.Builder;
import lombok.Getter;

import com.pandaterry.access_orchestrator.core.resource.SubjectId;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class Asset {
    private final AssetId id;
    private final String name;
    private final String type;
    private final String url;
    private final SubjectId ownerId;
    private final String state;
    private final Map<String, Object> metadata;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}