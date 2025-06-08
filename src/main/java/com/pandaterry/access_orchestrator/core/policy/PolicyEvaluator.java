package com.pandaterry.access_orchestrator.core.policy;

import com.pandaterry.access_orchestrator.core.context.Context;

public interface PolicyEvaluator {
    Policy.Effect evaluate(Policy policy, Context context);

    boolean canAccess(String subjectId, String resourceId, String action);

    boolean canAccessField(String subjectId, String resourceId, String field);
}