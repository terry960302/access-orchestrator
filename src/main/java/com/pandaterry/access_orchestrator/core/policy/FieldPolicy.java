package com.pandaterry.access_orchestrator.core.policy;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder(builderClassName = "FieldPolicyBuilder")
public class FieldPolicy {
    private final String id;
    private final String resourceType;
    private final String fieldName;
    private final List<Condition> conditions;
    private final Policy.Effect effect;
    private final String description;

    public boolean isAccessible() {
        return effect == Policy.Effect.ALLOW;
    }

    public static class FieldPolicyBuilder {
        public FieldPolicyBuilder accessible(boolean accessible) {
            this.effect(accessible ? Policy.Effect.ALLOW : Policy.Effect.DENY);
            return this;
        }
    }
}