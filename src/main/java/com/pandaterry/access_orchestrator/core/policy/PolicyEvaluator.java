package com.pandaterry.access_orchestrator.core.policy;

import com.pandaterry.access_orchestrator.core.context.Context;
import com.pandaterry.access_orchestrator.core.resource.SubjectId;
import com.pandaterry.access_orchestrator.core.resource.ResourceId;
import com.pandaterry.access_orchestrator.core.resource.FieldName;
import com.pandaterry.access_orchestrator.core.resource.Action;

public interface PolicyEvaluator {
    Policy.Effect evaluate(Policy policy, Context context);

    boolean canAccess(SubjectId subjectId, ResourceId resourceId, Action action);

    boolean canAccessField(SubjectId subjectId, ResourceId resourceId, FieldName field);
}