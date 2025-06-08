package com.pandaterry.access_orchestrator.core.policy;

import java.util.List;
import java.util.Map;

public interface FieldPolicyManager {
    void addFieldPolicy(FieldPolicy policy);

    void removeFieldPolicy(String resourceType, String fieldName);

    FieldPolicy getFieldPolicy(String resourceType, String fieldName);

    List<FieldPolicy> getFieldPolicies(String resourceType);

    Map<String, List<FieldPolicy>> getAllFieldPolicies();

    void clearFieldPolicies();
}