package com.pandaterry.access_orchestrator.core.context;

import com.pandaterry.access_orchestrator.core.resource.ResourceId;
import com.pandaterry.access_orchestrator.core.resource.SubjectId;
import com.pandaterry.access_orchestrator.core.resource.Action;
import com.pandaterry.access_orchestrator.core.attribute.AttributeId;
import com.pandaterry.access_orchestrator.core.attribute.AttributeValue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultContextManagerTest {
    private DefaultContextManager contextManager;

    @BeforeEach
    void setUp() {
        contextManager = new DefaultContextManager();
    }

    @Test
    @DisplayName("리소스와 액션을 포함한 키로 컨텍스트를 업데이트하면 캐시에 반영되어야 한다")
    void updateContext_ShouldUpdateCacheForKey() {
        SubjectId subjectId = new SubjectId("user1");
        ResourceId resourceId = new ResourceId("res1");
        Action action = Action.READ;

        // 기본 컨텍스트 로딩
        contextManager.getContext(subjectId, resourceId, action);

        Map<AttributeId, AttributeValue> subjectAttrs = new HashMap<>();
        subjectAttrs.put(new AttributeId("role"), new AttributeValue("DEV"));

        Context updated = Context.builder()
                .subject(Context.Subject.builder()
                        .id(subjectId)
                        .type("User")
                        .attributes(subjectAttrs)
                        .build())
                .resource(Context.Resource.builder()
                        .id(resourceId)
                        .type("Document")
                        .attributes(new HashMap<>())
                        .build())
                .environment(Context.Environment.builder()
                        .id("env1")
                        .type("System")
                        .attributes(new HashMap<>())
                        .build())
                .build();

        contextManager.updateContext(subjectId, resourceId, action, updated);

        Context result = contextManager.getContext(subjectId, resourceId, action);
        assertThat(result.getSubject().getAttributes().get(new AttributeId("role"))).isEqualTo(new AttributeValue("DEV"));
    }

    @Test
    @DisplayName("다른 리소스의 컨텍스트는 영향을 받지 않아야 한다")
    void updateContext_ShouldNotAffectDifferentKey() {
        SubjectId subjectId = new SubjectId("user1");
        ResourceId resource1 = new ResourceId("res1");
        ResourceId resource2 = new ResourceId("res2");
        Action action = Action.READ;

        contextManager.getContext(subjectId, resource1, action);
        contextManager.getContext(subjectId, resource2, action);

        Map<AttributeId, AttributeValue> attrs = new HashMap<>();
        attrs.put(new AttributeId("role"), new AttributeValue("DEV"));

        Context updated = Context.builder()
                .subject(Context.Subject.builder()
                        .id(subjectId)
                        .type("User")
                        .attributes(attrs)
                        .build())
                .resource(Context.Resource.builder()
                        .id(resource1)
                        .type("Document")
                        .attributes(new HashMap<>())
                        .build())
                .environment(Context.Environment.builder()
                        .id("env1")
                        .type("System")
                        .attributes(new HashMap<>())
                        .build())
                .build();

        contextManager.updateContext(subjectId, resource1, action, updated);

        Context res1 = contextManager.getContext(subjectId, resource1, action);
        Context res2 = contextManager.getContext(subjectId, resource2, action);

        assertThat(res1.getSubject().getAttributes().get(new AttributeId("role"))).isEqualTo(new AttributeValue("DEV"));
        assertThat(res2.getSubject().getAttributes().get(new AttributeId("role"))).isEqualTo(new AttributeValue("PM"));

    }
}
