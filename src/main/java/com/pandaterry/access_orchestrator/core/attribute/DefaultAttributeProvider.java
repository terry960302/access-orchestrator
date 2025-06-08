package com.pandaterry.access_orchestrator.core.attribute;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class DefaultAttributeProvider implements AttributeProvider {
    private final Map<String, Attribute> attributeCache = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Attribute>> subjectAttributes = new ConcurrentHashMap<>();

    @Override
    public Attribute getAttribute(String id) {
        return attributeCache.get(id);
    }

    @Override
    public Map<String, Attribute> getAttributes(String subjectId) {
        return subjectAttributes.computeIfAbsent(subjectId, k -> new ConcurrentHashMap<>());
    }

    @Override
    public void setAttribute(String id, Attribute attribute) {
        attributeCache.put(id, attribute);
    }

    @Override
    public void removeAttribute(String id) {
        attributeCache.remove(id);
        subjectAttributes.values().forEach(attributes -> attributes.remove(id));
    }
}