package com.pandaterry.access_orchestrator.core.context;

public interface ContextManager {
    Context getContext(String subjectId, String resourceId, String action);

    void updateContext(String subjectId, Context context);

    void clearContext(String subjectId);
}