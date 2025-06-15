package com.pandaterry.access_orchestrator.core.context;


import com.pandaterry.access_orchestrator.core.resource.SubjectId;
import com.pandaterry.access_orchestrator.core.resource.ResourceId;
import com.pandaterry.access_orchestrator.core.resource.Action;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;

import com.pandaterry.access_orchestrator.core.attribute.AttributeId;
import com.pandaterry.access_orchestrator.core.attribute.AttributeValue;

public class DefaultContextManager implements ContextManager {
    private final Map<String, Context> contextCache = new ConcurrentHashMap<>();

    @Override
    public Context getContext(SubjectId subjectId, ResourceId resourceId, Action action) {
        String key = generateKey(subjectId.value(), resourceId.value(), action);
        return contextCache.computeIfAbsent(key, k -> buildContext(subjectId, resourceId));
    }

    @Override
    public void updateContext(SubjectId subjectId, ResourceId resourceId, Action action, Context context) {
        String key = generateKey(subjectId.value(), resourceId.value(), action);
        contextCache.put(key, context);
    }

    @Override
    public void clearContext(SubjectId subjectId) {
        contextCache.entrySet().removeIf(entry -> entry.getKey().startsWith(subjectId.value() + ":"));
    }

    private String generateKey(String subjectId, String resourceId, Action action) {
        return String.format("%s:%s:%s", subjectId, resourceId, action.name().toLowerCase());
    }

    private Context buildContext(SubjectId subjectId, ResourceId resourceId) {
        // 테스트를 위한 기본 Context 생성
        Map<AttributeId, AttributeValue> subjectAttributes = new HashMap<>();
        subjectAttributes.put(new AttributeId("role"), new AttributeValue("PM"));
        subjectAttributes.put(new AttributeId("domain"), new AttributeValue("FINANCE"));
        subjectAttributes.put(new AttributeId("title"), new AttributeValue("PM"));

        Map<AttributeId, AttributeValue> resourceAttributes = new HashMap<>();
        resourceAttributes.put(new AttributeId("type"), new AttributeValue("Document"));

        Map<AttributeId, AttributeValue> environmentAttributes = new HashMap<>();
        environmentAttributes.put(new AttributeId("timezone"), new AttributeValue("UTC"));

        return Context.builder()
                .subject(Context.Subject.builder()
                        .id(subjectId)
                        .type("User")
                        .attributes(subjectAttributes)
                        .build())
                .resource(Context.Resource.builder()
                        .id(resourceId)
                        .type("Document")
                        .attributes(resourceAttributes)
                        .build())
                .environment(Context.Environment.builder()
                        .id("env1")
                        .type("System")
                        .attributes(environmentAttributes)
                        .build())
                .build();
    }
}