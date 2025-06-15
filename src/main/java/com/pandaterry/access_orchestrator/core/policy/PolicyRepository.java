package com.pandaterry.access_orchestrator.core.policy;

import java.util.List;
import com.pandaterry.access_orchestrator.core.resource.Action;

public interface PolicyRepository {
    void addPolicy(String resourceType, Action action, Policy policy);

    List<Policy> getPolicies(String resourceType, Action action);

    void clearPolicies();
}
