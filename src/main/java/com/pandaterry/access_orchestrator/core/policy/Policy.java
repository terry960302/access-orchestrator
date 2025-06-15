package com.pandaterry.access_orchestrator.core.policy;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class Policy {
    private final String id;
    private final int priority;
    private final List<Condition> conditions;
    private final Effect effect;


    public enum Effect {
        ALLOW,
        DENY
    }
}
