package com.pandaterry.access_orchestrator.core.attribute;

import java.util.Map;

import com.pandaterry.access_orchestrator.core.resource.SubjectId;

public interface AttributeProvider {
    Attribute getAttribute(AttributeId id);

    Map<AttributeId, Attribute> getAttributes(SubjectId subjectId);

    void setAttribute(AttributeId id, Attribute attribute);

    void removeAttribute(AttributeId id);
}