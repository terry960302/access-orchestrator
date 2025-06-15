package com.pandaterry.access_orchestrator.core.context;

import lombok.Builder;
import lombok.Getter;

import com.pandaterry.access_orchestrator.core.resource.SubjectId;
import com.pandaterry.access_orchestrator.core.resource.ResourceId;
import com.pandaterry.access_orchestrator.core.attribute.AttributeId;
import com.pandaterry.access_orchestrator.core.attribute.AttributeValue;

import java.util.Map;

@Getter
@Builder
public class Context {
    private Subject subject;
    private Resource resource;
    private Environment environment;

    @Getter
    @Builder
    public static class Subject {
        private SubjectId id;
        private String type;
        private Map<AttributeId, AttributeValue> attributes;
    }

    @Getter
    @Builder
    public static class Resource {
        private ResourceId id;
        private String type;
        private Map<AttributeId, AttributeValue> attributes;
    }

    @Getter
    @Builder
    public static class Environment {
        private String id;
        private String type;
        private Map<AttributeId, AttributeValue> attributes;
    }
}