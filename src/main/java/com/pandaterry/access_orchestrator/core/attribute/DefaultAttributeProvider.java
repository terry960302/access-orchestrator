package com.pandaterry.access_orchestrator.core.attribute;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.pandaterry.access_orchestrator.core.resource.SubjectId;

public class DefaultAttributeProvider implements AttributeProvider {
    private final Map<AttributeId, Attribute> attributeCache = new ConcurrentHashMap<>();
    private final Map<SubjectId, Map<AttributeId, Attribute>> subjectAttributes = new ConcurrentHashMap<>();

    @Override
    public Attribute getAttribute(AttributeId id) {
        return attributeCache.get(id);
    }

    @Override
    public Map<AttributeId, Attribute> getAttributes(SubjectId subjectId) {
        return subjectAttributes.computeIfAbsent(subjectId, k -> new ConcurrentHashMap<>());
    }

    @Override
    public void setAttribute(AttributeId id, Attribute attribute) {
        attributeCache.put(id, attribute);
    }

    @Override
    public void removeAttribute(AttributeId id) {
        attributeCache.remove(id);
        subjectAttributes.values().forEach(attributes -> attributes.remove(id));
    }
}