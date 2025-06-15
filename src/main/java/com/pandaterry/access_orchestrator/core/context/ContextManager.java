package com.pandaterry.access_orchestrator.core.context;

import com.pandaterry.access_orchestrator.core.resource.ResourceId;
import com.pandaterry.access_orchestrator.core.resource.SubjectId;
import com.pandaterry.access_orchestrator.core.resource.Action;

public interface ContextManager {
    Context getContext(SubjectId subjectId, ResourceId resourceId, Action action);

    void updateContext(SubjectId subjectId, ResourceId resourceId, Action action, Context context);

    void clearContext(SubjectId subjectId);
}