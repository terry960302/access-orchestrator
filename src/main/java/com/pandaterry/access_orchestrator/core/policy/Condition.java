package com.pandaterry.access_orchestrator.core.policy;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Condition {
    private String attributeId;
    private Operator operator;
    private Object value;
    private final LogicalOperator logicalOperator;

    public enum Operator {
        EQUALS,
        NOT_EQUALS,
        CONTAINS,
        NOT_CONTAINS,
        GREATER_THAN,
        LESS_THAN,
        GREATER_THAN_OR_EQUALS,
        LESS_THAN_OR_EQUALS,
        IN,
        NOT_IN
    }

    public enum LogicalOperator {
        AND,
        OR
    }
}