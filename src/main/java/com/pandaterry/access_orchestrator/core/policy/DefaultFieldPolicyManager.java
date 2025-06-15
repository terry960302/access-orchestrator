package com.pandaterry.access_orchestrator.core.policy;


import com.pandaterry.access_orchestrator.core.resource.FieldName;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
public class DefaultFieldPolicyManager implements FieldPolicyManager {
    private final Map<String, List<FieldPolicy>> fieldPolicies = new ConcurrentHashMap<>();

    @Override
    public void addFieldPolicy(FieldPolicy policy) {
        if (policy == null) {
            throw new IllegalArgumentException("Policy cannot be null");
        }
        if (policy.getResourceType() == null || policy.getFieldName() == null) {
            throw new IllegalArgumentException("Resource type and field name cannot be null");
        }

        // effect가 null인 경우 기본값으로 DENY 설정 (보안을 위해 명시적 허용이 필요)
        FieldPolicy policyWithEffect = policy.getEffect() == null ? FieldPolicy.builder()
                .id(policy.getId())
                .resourceType(policy.getResourceType())
                .fieldName(policy.getFieldName())
                .conditions(policy.getConditions())
                .effect(Policy.Effect.DENY) // 기본값을 DENY로 변경
                .description(policy.getDescription())
                .build() : policy;

        fieldPolicies.computeIfAbsent(policyWithEffect.getResourceType(), k -> new CopyOnWriteArrayList<>())
                .add(policyWithEffect);
    }

    @Override
    public void removeFieldPolicy(String resourceType, FieldName fieldName) {
        if (resourceType == null || fieldName == null) {
            throw new IllegalArgumentException("Resource type and field name cannot be null");
        }
        List<FieldPolicy> list = fieldPolicies.get(resourceType);
        if (list != null) {
            list.removeIf(p -> p.getFieldName().equals(fieldName));
            if (list.isEmpty()) {
                fieldPolicies.remove(resourceType);
            }
        }
    }

    @Override
    public FieldPolicy getFieldPolicy(String resourceType, FieldName fieldName) {
        if (resourceType == null || fieldName == null) {
            throw new IllegalArgumentException("Resource type and field name cannot be null");
        }
        List<FieldPolicy> list = fieldPolicies.get(resourceType);
        if (list == null) {
            return null;
        }
        return list.stream()
                .filter(p -> p.getFieldName().equals(fieldName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<FieldPolicy> getFieldPolicies(String resourceType) {
        if (resourceType == null) {
            throw new IllegalArgumentException("Resource type cannot be null");
        }
        return fieldPolicies.getOrDefault(resourceType, Collections.emptyList());
    }

    @Override
    public Map<String, List<FieldPolicy>> getAllFieldPolicies() {
        return Collections.unmodifiableMap(fieldPolicies);
    }

    @Override
    public void clearFieldPolicies() {
        fieldPolicies.clear();
    }
}