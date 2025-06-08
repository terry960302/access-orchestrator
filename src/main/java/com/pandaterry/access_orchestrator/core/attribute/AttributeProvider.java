package com.pandaterry.access_orchestrator.core.attribute;

import java.util.Map;

public interface AttributeProvider {
    Attribute getAttribute(String id);

    Map<String, Attribute> getAttributes(String subjectId);

    void setAttribute(String id, Attribute attribute);

    void removeAttribute(String id);
}