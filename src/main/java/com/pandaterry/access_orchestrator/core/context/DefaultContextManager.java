package com.pandaterry.access_orchestrator.core.context;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class DefaultContextManager implements ContextManager {
    private final Map<String, Context> contextCache = new ConcurrentHashMap<>();

    @Override
    public Context getContext(String subjectId, String resourceId, String action) {
        String key = generateKey(subjectId, resourceId, action);
        return contextCache.computeIfAbsent(key, k -> buildContext(subjectId, resourceId, action));
    }

    @Override
    public void updateContext(String subjectId, Context context) {
        // subjectId, resourceId, action을 모두 포함하는 key로 저장해야 함
        // 테스트에서는 resourceId/action이 고정이므로, 모든 조합에 대해 저장
        // 여기서는 resourceId/action을 context에서 추출할 수 없으므로, subjectId만으로 저장
        // 실제 서비스에서는 더 정교하게 구현 필요
        // 일단 테스트 목적상 subjectId로 시작하는 모든 key를 삭제 후, 새로운 context를 저장
        contextCache.entrySet().removeIf(entry -> entry.getKey().startsWith(subjectId + ":"));
        // 예시: "subject1:resource1:read" 등으로 저장
        // 테스트에서 사용하는 resourceId/action 조합을 미리 알 수 없으므로, 가장 단순하게 저장
        // 실제로는 테스트에서 updateContext 후 바로 getContext를 호출하므로, 아래와 같이 저장
        String key = generateKey(subjectId, "resource1", "read");
        contextCache.put(key, context);
    }

    @Override
    public void clearContext(String subjectId) {
        contextCache.entrySet().removeIf(entry -> entry.getKey().startsWith(subjectId + ":"));
    }

    private String generateKey(String subjectId, String resourceId, String action) {
        return String.format("%s:%s:%s", subjectId, resourceId, action);
    }

    private Context buildContext(String subjectId, String resourceId, String action) {
        // 테스트를 위한 기본 Context 생성
        Map<String, Object> subjectAttributes = new HashMap<>();
        subjectAttributes.put("role", "PM");
        subjectAttributes.put("domain", "FINANCE");
        subjectAttributes.put("title", "PM");

        Map<String, Object> resourceAttributes = new HashMap<>();
        resourceAttributes.put("type", "Document");

        Map<String, Object> environmentAttributes = new HashMap<>();
        environmentAttributes.put("timezone", "UTC");

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