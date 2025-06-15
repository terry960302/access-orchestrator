package com.pandaterry.access_orchestrator.core.policy;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import com.pandaterry.access_orchestrator.core.resource.Action;

public class DefaultPolicyRepository implements PolicyRepository {
    private final Map<String, Map<Action, List<Policy>>> policies = new ConcurrentHashMap<>();

    @Override
    public void addPolicy(String resourceType, Action action, Policy policy) {
        if (resourceType == null || action == null || policy == null) {
            throw new IllegalArgumentException("resourceType, action and policy cannot be null");
        }
        policies
            .computeIfAbsent(resourceType, k -> new ConcurrentHashMap<>())
            .computeIfAbsent(action, k -> new ArrayList<>())
            .add(policy);
    }

    @Override
    public List<Policy> getPolicies(String resourceType, Action action) {
        if (resourceType == null || action == null) {
            throw new IllegalArgumentException("resourceType and action cannot be null");
        }
        return policies.getOrDefault(resourceType, Collections.emptyMap())
                .getOrDefault(action, Collections.emptyList());
    }

    @Override
    public void clearPolicies() {
        policies.clear();
    }
}
