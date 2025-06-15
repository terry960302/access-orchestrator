package com.pandaterry.access_orchestrator.core.policy;

import com.pandaterry.access_orchestrator.core.attribute.Attribute;
import com.pandaterry.access_orchestrator.core.attribute.AttributeProvider;
import com.pandaterry.access_orchestrator.core.attribute.AttributeId;
import com.pandaterry.access_orchestrator.core.attribute.AttributeValue;
import com.pandaterry.access_orchestrator.core.context.Context;
import com.pandaterry.access_orchestrator.core.context.ContextManager;
import com.pandaterry.access_orchestrator.core.resource.SubjectId;
import com.pandaterry.access_orchestrator.core.resource.ResourceId;
import com.pandaterry.access_orchestrator.core.resource.FieldName;
import com.pandaterry.access_orchestrator.core.resource.Action;

import java.util.Map;
import java.util.List;

public class DefaultPolicyEvaluator implements PolicyEvaluator {
    private final ContextManager contextManager;
    private final AttributeProvider attributeProvider;
    private final FieldPolicyManager fieldPolicyManager;
    private final PolicyRepository policyRepository;

    public DefaultPolicyEvaluator(ContextManager contextManager,
                                  AttributeProvider attributeProvider,
                                  FieldPolicyManager fieldPolicyManager,
                                  PolicyRepository policyRepository) {
        this.contextManager = contextManager;
        this.attributeProvider = attributeProvider;
        this.fieldPolicyManager = fieldPolicyManager;
        this.policyRepository = policyRepository;
    }

    @Override
    public Policy.Effect evaluate(Policy policy, Context context) {
        if (policy.getConditions() == null || policy.getConditions().isEmpty()) {
            return policy.getEffect();
        }

        boolean conditionsMet = evaluateConditions(policy.getConditions(), context);

        return conditionsMet ? policy.getEffect() : Policy.Effect.DENY;
    }

    @Override
    public boolean canAccess(SubjectId subjectId, ResourceId resourceId, Action action) {
        Context context = contextManager.getContext(subjectId, resourceId, action);
        if (context == null) {
            return false;
        }

        String resourceType = context.getResource().getType();
        var policies = policyRepository.getPolicies(resourceType, action);

        boolean allow = false;
        for (Policy policy : policies) {
            Policy.Effect effect = evaluate(policy, context);
            if (effect == Policy.Effect.DENY) {
                return false;
            }
            if (effect == Policy.Effect.ALLOW) {
                allow = true;
            }
        }

        return allow;
    }

    @Override
    public boolean canAccessField(SubjectId subjectId, ResourceId resourceId, FieldName field) {


        Context context = retrieveContext(subjectId, resourceId);

        if (context == null) {
            return false;
        }

        FieldPolicy fieldPolicy = findFieldPolicy(context, field);

        if (fieldPolicy == null) {
            return true; // 정책이 없으면 기본적으로 접근 허용
        }

        if (!fieldPolicy.isAccessible()) {
            return false;
        }

        return checkConditions(fieldPolicy, context);
    }

    private Context retrieveContext(SubjectId subjectId, ResourceId resourceId) {
        return contextManager.getContext(subjectId, resourceId, Action.READ);
    }

    private FieldPolicy findFieldPolicy(Context context, FieldName field) {
        String resourceType = context.getResource().getType();
        return fieldPolicyManager.getFieldPolicy(resourceType, field);
    }

    private boolean checkConditions(FieldPolicy fieldPolicy, Context context) {
        if (fieldPolicy.getConditions() == null || fieldPolicy.getConditions().isEmpty()) {
            return true;
        }

        return evaluateConditions(fieldPolicy.getConditions(), context);
    }

    private boolean evaluateConditions(List<Condition> conditions, Context context) {
        if (conditions == null || conditions.isEmpty()) {
            return true;
        }

        boolean result = evaluateCondition(conditions.get(0), context);

        for (int i = 1; i < conditions.size(); i++) {
            Condition condition = conditions.get(i);
            boolean current = evaluateCondition(condition, context);

            Condition.LogicalOperator operator = condition.getLogicalOperator();
            if (operator == Condition.LogicalOperator.OR) {
                result = result || current;
            } else {
                result = result && current;
            }
        }

        return result;
    }

    private boolean evaluateCondition(Condition condition, Context context) {
        AttributeId attributeId = condition.getAttributeId();
        Attribute attribute = attributeProvider.getAttribute(attributeId);


        if (attribute == null) {
            return false;
        }

        Object attributeValue = getAttributeValue(attribute, context);

        boolean result = attribute.matches(condition, attributeValue);

        return result;
    }

    private Object getAttributeValue(Attribute attribute, Context context) {
        Map<AttributeId, AttributeValue> subjectAttributes = context.getSubject().getAttributes();
        Map<AttributeId, AttributeValue> resourceAttributes = context.getResource().getAttributes();
        Map<AttributeId, AttributeValue> environmentAttributes = context.getEnvironment().getAttributes();

        AttributeValue value = switch (attribute.getSource()) {
            case SUBJECT -> subjectAttributes.get(attribute.getId());
            case RESOURCE -> resourceAttributes.get(attribute.getId());
            case ENVIRONMENT -> environmentAttributes.get(attribute.getId());
        };

        return value != null ? value.value() : null;
    }

}