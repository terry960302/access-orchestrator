package com.pandaterry.access_orchestrator.core.policy;

import com.pandaterry.access_orchestrator.core.attribute.Attribute;
import com.pandaterry.access_orchestrator.core.attribute.AttributeProvider;
import com.pandaterry.access_orchestrator.core.context.Context;
import com.pandaterry.access_orchestrator.core.context.ContextManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DefaultPolicyEvaluator implements PolicyEvaluator {
    private final ContextManager contextManager;
    private final AttributeProvider attributeProvider;
    private final FieldPolicyManager fieldPolicyManager;

    @Override
    public Policy.Effect evaluate(Policy policy, Context context) {
        if (policy.getConditions() == null || policy.getConditions().isEmpty()) {
            return policy.getEffect();
        }

        boolean allConditionsMet = policy.getConditions().stream()
                .allMatch(condition -> evaluateCondition(condition, context));

        return allConditionsMet ? policy.getEffect() : Policy.Effect.DENY;
    }

    @Override
    public boolean canAccess(String subjectId, String resourceId, String action) {
        Context context = contextManager.getContext(subjectId, resourceId, action);
        if (context == null) {
            return false;
        }

        // TODO: 실제 정책 평가 로직 구현
        // 현재는 임시로 true 반환
        return true;
    }

    @Override
    public boolean canAccessField(String subjectId, String resourceId, String field) {
        System.out.println("Checking access for field: " + field); // 필드 접근 체크 시작
        System.out.println("Subject ID: " + subjectId); // 주체 ID
        System.out.println("Resource ID: " + resourceId); // 리소스 ID

        Context context = contextManager.getContext(subjectId, resourceId, "read");
        System.out.println("Context: " + context); // 컨텍스트

        if (context == null) {
            System.out.println("Context is null"); // 컨텍스트가 null인 경우
            return false;
        }

        String resourceType = context.getResource().getType();
        System.out.println("Resource type: " + resourceType); // 리소스 타입

        FieldPolicy fieldPolicy = fieldPolicyManager.getFieldPolicy(resourceType, field);
        System.out.println("Field policy: " + fieldPolicy); // 필드 정책

        if (fieldPolicy == null) {
            System.out.println("No field policy found"); // 필드 정책이 없는 경우
            return true; // 정책이 없으면 기본적으로 접근 허용
        }

        if (!fieldPolicy.isAccessible()) {
            System.out.println("Field policy is not accessible"); // 필드 정책이 접근 불가능한 경우
            return false;
        }

        if (fieldPolicy.getConditions() == null || fieldPolicy.getConditions().isEmpty()) {
            System.out.println("No conditions in field policy"); // 조건이 없는 경우
            return true;
        }

        System.out.println("Evaluating conditions: " + fieldPolicy.getConditions()); // 조건 평가 시작
        return fieldPolicy.getConditions().stream()
                .allMatch(condition -> evaluateCondition(condition, context));
    }

    private boolean evaluateCondition(Condition condition, Context context) {
        String attributeId = condition.getAttributeId();
        Attribute attribute = attributeProvider.getAttribute(attributeId);

        System.out.println("Evaluating condition for attribute: " + attributeId); // 속성 ID 출력
        System.out.println("Found attribute: " + attribute); // 찾은 속성 출력

        if (attribute == null) {
            System.out.println("Attribute not found: " + attributeId); // 속성을 찾지 못한 경우
            return false;
        }

        Object attributeValue = getAttributeValue(attribute, context);
        Object expectedValue = condition.getValue();

        System.out.println("Attribute value: " + attributeValue); // 실제 속성 값 출력
        System.out.println("Expected value: " + expectedValue); // 기대하는 값 출력

        boolean result = switch (condition.getOperator()) {
            case EQUALS -> Objects.equals(attributeValue, expectedValue);
            case NOT_EQUALS -> !Objects.equals(attributeValue, expectedValue);
            case CONTAINS -> attributeValue != null &&
                    expectedValue != null &&
                    attributeValue.toString().contains(expectedValue.toString());
            case NOT_CONTAINS -> attributeValue != null &&
                    expectedValue != null &&
                    !attributeValue.toString().contains(expectedValue.toString());
            case GREATER_THAN -> compareValues(attributeValue, expectedValue) > 0;
            case LESS_THAN -> compareValues(attributeValue, expectedValue) < 0;
            case GREATER_THAN_OR_EQUALS -> compareValues(attributeValue, expectedValue) >= 0;
            case LESS_THAN_OR_EQUALS -> compareValues(attributeValue, expectedValue) <= 0;
            case IN -> attributeValue != null &&
                    expectedValue instanceof Iterable &&
                    containsInIterable((Iterable<?>) expectedValue, attributeValue);
            case NOT_IN -> attributeValue != null &&
                    expectedValue instanceof Iterable &&
                    !containsInIterable((Iterable<?>) expectedValue, attributeValue);
        };

        System.out.println("Condition evaluation result: " + result); // 조건 평가 결과 출력
        return result;
    }

    private Object getAttributeValue(Attribute attribute, Context context) {
        Map<String, Object> subjectAttributes = context.getSubject().getAttributes();
        Map<String, Object> resourceAttributes = context.getResource().getAttributes();
        Map<String, Object> environmentAttributes = context.getEnvironment().getAttributes();

        System.out.println("Getting value for attribute: " + attribute.getId()); // 속성 ID 출력
        System.out.println("Subject attributes: " + subjectAttributes); // 주체 속성 출력
        System.out.println("Resource attributes: " + resourceAttributes); // 리소스 속성 출력
        System.out.println("Environment attributes: " + environmentAttributes); // 환경 속성 출력

        Object value = switch (attribute.getSource()) {
            case SUBJECT -> subjectAttributes.get(attribute.getId());
            case RESOURCE -> resourceAttributes.get(attribute.getId());
            case ENVIRONMENT -> environmentAttributes.get(attribute.getId());
        };

        System.out.println("Found value: " + value); // 찾은 값 출력
        return value;
    }

    @SuppressWarnings("unchecked")
    private int compareValues(Object value1, Object value2) {
        if (value1 == null || value2 == null) {
            return 0;
        }

        if (value1 instanceof Comparable && value2 instanceof Comparable) {
            return ((Comparable<Object>) value1).compareTo(value2);
        }

        return value1.toString().compareTo(value2.toString());
    }

    private boolean containsInIterable(Iterable<?> iterable, Object value) {
        for (Object element : iterable) {
            if (Objects.equals(element, value)) {
                return true;
            }
        }
        return false;
    }
}