package com.pandaterry.access_orchestrator.core.attribute;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.Objects;

import com.pandaterry.access_orchestrator.core.policy.Condition;
import com.pandaterry.access_orchestrator.core.attribute.AttributeId;

@Getter
@Builder
public class Attribute {
    private final AttributeId id;
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

    public boolean matches(Condition condition, Object contextValue) {
        Object expectedValue = condition.getValue();

        return switch (condition.getOperator()) {
            case EQUALS -> Objects.equals(contextValue, expectedValue);
            case NOT_EQUALS -> !Objects.equals(contextValue, expectedValue);
            case CONTAINS -> contextValue != null && expectedValue != null &&
                    contextValue.toString().contains(expectedValue.toString());
            case NOT_CONTAINS -> contextValue != null && expectedValue != null &&
                    !contextValue.toString().contains(expectedValue.toString());
            case GREATER_THAN -> compareValues(contextValue, expectedValue) > 0;
            case LESS_THAN -> compareValues(contextValue, expectedValue) < 0;
            case GREATER_THAN_OR_EQUALS -> compareValues(contextValue, expectedValue) >= 0;
            case LESS_THAN_OR_EQUALS -> compareValues(contextValue, expectedValue) <= 0;
            case IN -> contextValue != null && expectedValue instanceof Iterable &&
                    containsInIterable((Iterable<?>) expectedValue, contextValue);
            case NOT_IN -> contextValue != null && expectedValue instanceof Iterable &&
                    !containsInIterable((Iterable<?>) expectedValue, contextValue);
        };
    }

    @SuppressWarnings("unchecked")
    private int compareValues(Object value1, Object value2) {
        if (value1 == null || value2 == null) {
            return 0;
        }

        if (value1 instanceof Comparable && value2 instanceof Comparable) {
            return ((Comparable<Object>) value1).compareTo(value2);
        }

        return value1.toString().compareTo(value2.toString());
    }

    private boolean containsInIterable(Iterable<?> iterable, Object value) {
        for (Object element : iterable) {
            if (Objects.equals(element, value)) {
                return true;
            }
        }
        return false;
    }
}