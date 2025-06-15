package com.pandaterry.access_orchestrator.core.policy;

import com.pandaterry.access_orchestrator.core.resource.FieldName;

import java.util.List;
import java.util.Map;

public interface FieldPolicyManager {
    void addFieldPolicy(FieldPolicy policy);

    void removeFieldPolicy(String resourceType, FieldName fieldName);

    FieldPolicy getFieldPolicy(String resourceType, FieldName fieldName);

    List<FieldPolicy> getFieldPolicies(String resourceType);

    Map<String, List<FieldPolicy>> getAllFieldPolicies();

    void clearFieldPolicies();
}