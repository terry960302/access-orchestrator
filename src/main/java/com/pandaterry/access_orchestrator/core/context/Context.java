package com.pandaterry.access_orchestrator.core.context;

import lombok.Builder;
import lombok.Getter;

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
        private String id;
        private String type;
        private Map<String, Object> attributes;
    }

    @Getter
    @Builder
    public static class Resource {
        private String id;
        private String type;
        private Map<String, Object> attributes;
    }

    @Getter
    @Builder
    public static class Environment {
        private String id;
        private String type;
        private Map<String, Object> attributes;
    }
}