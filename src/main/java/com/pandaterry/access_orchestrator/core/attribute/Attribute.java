package com.pandaterry.access_orchestrator.core.attribute;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class Attribute {
    private final String id;
    private final AttributeType type;
    private final Object value;
    private final Map<String, Object> metadata;
    private String name;
    private String description;
    private Source source;
    private DataType dataType;

    public enum AttributeType {
        STRING,
        NUMBER,
        BOOLEAN,
        DATE,
        ENUM,
        OBJECT,
        ARRAY
    }

    public enum Source {
        SUBJECT,
        RESOURCE,
        ENVIRONMENT
    }

    public enum DataType {
        STRING,
        NUMBER,
        BOOLEAN,
        DATE,
        LIST,
        MAP
    }
}