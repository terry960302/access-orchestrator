package com.pandaterry.access_orchestrator;

import com.pandaterry.access_orchestrator.core.attribute.Attribute;
import com.pandaterry.access_orchestrator.core.attribute.AttributeProvider;
import com.pandaterry.access_orchestrator.core.attribute.AttributeId;
import com.pandaterry.access_orchestrator.core.attribute.AttributeValue;
import com.pandaterry.access_orchestrator.core.context.Context;
import com.pandaterry.access_orchestrator.core.context.ContextManager;
import com.pandaterry.access_orchestrator.core.policy.*;
import com.pandaterry.access_orchestrator.core.resource.FieldName;
import com.pandaterry.access_orchestrator.core.resource.SubjectId;
import com.pandaterry.access_orchestrator.core.resource.ResourceId;
import com.pandaterry.access_orchestrator.core.resource.Action;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultPolicyEvaluatorTest {
        @Mock
        private ContextManager contextManager;
        @Mock
        private AttributeProvider attributeProvider;
        @Mock
        private FieldPolicyManager fieldPolicyManager;
        @Mock
        private PolicyRepository policyRepository;

        private DefaultPolicyEvaluator evaluator;

        @BeforeEach
        void setUp() {
                evaluator = new DefaultPolicyEvaluator(contextManager, attributeProvider, fieldPolicyManager, policyRepository);
        }

        @Test
        void evaluate_WhenNoConditions_ShouldReturnPolicyEffect() {
                // given
                Policy policy = Policy.builder()
                                .effect(Policy.Effect.ALLOW)
                                .build();
                Context context = createTestContext();

                // when
                Policy.Effect result = evaluator.evaluate(policy, context);

                // then
                assertThat(result).isEqualTo(Policy.Effect.ALLOW);
        }

        @Test
        void evaluate_WhenAllConditionsMet_ShouldReturnPolicyEffect() {
                // given
                Policy policy = Policy.builder()
                                .effect(Policy.Effect.ALLOW)
                                .conditions(List.of(
                                                Condition.builder()
                                                                .attributeId(new AttributeId("role"))
                                                                .operator(Condition.Operator.EQUALS)
                                                                .value("ADMIN")
                                                                .build()))
                                .build();
                Context context = createTestContextWithRole("ADMIN");
                Attribute attribute = Attribute.builder()
                                .id(new AttributeId("role"))
                                .source(Attribute.Source.SUBJECT)
                                .build();

                when(attributeProvider.getAttribute(new AttributeId("role"))).thenReturn(attribute);

                // when
                Policy.Effect result = evaluator.evaluate(policy, context);

                // then
                assertThat(result).isEqualTo(Policy.Effect.ALLOW);
        }

        @Test
        void evaluate_WhenAnyConditionNotMet_ShouldReturnDeny() {
                // given
                Policy policy = Policy.builder()
                                .effect(Policy.Effect.ALLOW)
                                .conditions(List.of(
                                                Condition.builder()
                                                                .attributeId(new AttributeId("role"))
                                                                .operator(Condition.Operator.EQUALS)
                                                                .value("ADMIN")
                                                                .build()))
                                .build();
                Context context = createTestContext();
                Attribute attribute = Attribute.builder()
                                .id(new AttributeId("role"))
                                .source(Attribute.Source.SUBJECT)
                                .build();

                when(attributeProvider.getAttribute(new AttributeId("role"))).thenReturn(attribute);

                // when
                Policy.Effect result = evaluator.evaluate(policy, context);

                // then
                assertThat(result).isEqualTo(Policy.Effect.DENY);
        }

        @Test
        void evaluate_WithOrOperator_ShouldReturnPolicyEffectWhenAnyConditionMet() {
                // given
                Policy policy = Policy.builder()
                                .effect(Policy.Effect.ALLOW)
                                .conditions(List.of(
                                                Condition.builder()
                                                                .attributeId(new AttributeId("role"))
                                                                .operator(Condition.Operator.EQUALS)
                                                                .value("USER")
                                                                .build(),
                                                Condition.builder()
                                                                .attributeId(new AttributeId("role"))
                                                                .operator(Condition.Operator.EQUALS)
                                                                .value("ADMIN")
                                                                .logicalOperator(Condition.LogicalOperator.OR)
                                                                .build()))
                                .build();
                Context context = createTestContextWithRole("ADMIN");
                Attribute attribute = Attribute.builder()
                                .id(new AttributeId("role"))
                                .source(Attribute.Source.SUBJECT)
                                .build();

                when(attributeProvider.getAttribute(new AttributeId("role"))).thenReturn(attribute);

                // when
                Policy.Effect result = evaluator.evaluate(policy, context);

                // then
                assertThat(result).isEqualTo(Policy.Effect.ALLOW);
        }

        @Test
        void canAccessField_WhenNoPolicy_ShouldReturnTrue() {
                // given
                Context context = createTestContext();
                when(contextManager.getContext(any(SubjectId.class), any(ResourceId.class), any(Action.class))).thenReturn(context);
                when(fieldPolicyManager.getFieldPolicy(any(), any(FieldName.class))).thenReturn(null);

                // when
                boolean result = evaluator.canAccessField(new SubjectId("subject1"), new ResourceId("resource1"), new FieldName("field1"));

                // then
                assertThat(result).isTrue();
        }

        @Test
        void canAccessField_WhenPolicyExistsAndAccessible_ShouldReturnTrue() {
                // given
                Context context = createTestContext();
                FieldPolicy fieldPolicy = FieldPolicy.builder()
                                .resourceType("Document")
                                .fieldName(new FieldName("field1"))
                                .accessible(true)
                                .build();

                when(contextManager.getContext(any(SubjectId.class), any(ResourceId.class), any(Action.class))).thenReturn(context);
                when(fieldPolicyManager.getFieldPolicy(any(), any(FieldName.class))).thenReturn(fieldPolicy);

                // when
                boolean result = evaluator.canAccessField(new SubjectId("subject1"), new ResourceId("resource1"), new FieldName("field1"));

                // then
                assertThat(result).isTrue();
        }

        @Test
        void canAccessField_WhenPolicyExistsAndNotAccessible_ShouldReturnFalse() {
                // given
                Context context = createTestContext();
                FieldPolicy fieldPolicy = FieldPolicy.builder()
                                .resourceType("Document")
                                .fieldName(new FieldName("field1"))
                                .accessible(false)
                                .build();

                when(contextManager.getContext(any(SubjectId.class), any(ResourceId.class), any(Action.class))).thenReturn(context);
                when(fieldPolicyManager.getFieldPolicy(any(), any(FieldName.class))).thenReturn(fieldPolicy);

                // when
                boolean result = evaluator.canAccessField(new SubjectId("subject1"), new ResourceId("resource1"), new FieldName("field1"));

                // then
                assertThat(result).isFalse();
        }

        @Test
        void canAccessField_WithOrCondition_ShouldReturnTrueWhenAnyConditionMet() {
                // given
                Context context = createTestContext();
                FieldPolicy fieldPolicy = FieldPolicy.builder()
                                .resourceType("Document")
                                .fieldName(new FieldName("field1"))
                                .conditions(List.of(
                                                Condition.builder()
                                                                .attributeId(new AttributeId("role"))
                                                                .operator(Condition.Operator.EQUALS)
                                                                .value("ADMIN")
                                                                .build(),
                                                Condition.builder()
                                                                .attributeId(new AttributeId("role"))
                                                                .operator(Condition.Operator.EQUALS)
                                                                .value("USER")
                                                                .logicalOperator(Condition.LogicalOperator.OR)
                                                                .build()))
                                .accessible(true)
                                .build();

                when(contextManager.getContext(any(SubjectId.class), any(ResourceId.class), any(Action.class))).thenReturn(context);
                when(fieldPolicyManager.getFieldPolicy(any(), any(FieldName.class))).thenReturn(fieldPolicy);
                Attribute attribute = Attribute.builder().id(new AttributeId("role")).source(Attribute.Source.SUBJECT).build();
                when(attributeProvider.getAttribute(new AttributeId("role"))).thenReturn(attribute);

                // when
                boolean result = evaluator.canAccessField(new SubjectId("subject1"), new ResourceId("resource1"), new FieldName("field1"));

                // then
                assertThat(result).isTrue();
        }

        @Test
        void canAccess_WhenPolicyAllows_ShouldReturnTrue() {
                // given
                Context context = createTestContextWithRole("ADMIN");
                Policy policy = Policy.builder()
                                .effect(Policy.Effect.ALLOW)
                                .conditions(List.of(
                                                Condition.builder()
                                                                .attributeId(new AttributeId("role"))
                                                                .operator(Condition.Operator.EQUALS)
                                                                .value("ADMIN")
                                                                .build()))
                                .build();

                when(contextManager.getContext(any(SubjectId.class), any(ResourceId.class), any())).thenReturn(context);
                when(policyRepository.getPolicies(any(), any(Action.class))).thenReturn(List.of(policy));
                Attribute attribute = Attribute.builder().id(new AttributeId("role")).source(Attribute.Source.SUBJECT).build();
                when(attributeProvider.getAttribute(new AttributeId("role"))).thenReturn(attribute);

                // when
                boolean result = evaluator.canAccess(new SubjectId("subject1"), new ResourceId("resource1"), Action.READ);

                // then
                assertThat(result).isTrue();
        }

        @Test
        void canAccess_WhenPolicyDenies_ShouldReturnFalse() {
                // given
                Context context = createTestContextWithRole("USER");
                Policy policy = Policy.builder()
                                .effect(Policy.Effect.DENY)
                                .conditions(List.of(
                                                Condition.builder()
                                                                .attributeId(new AttributeId("role"))
                                                                .operator(Condition.Operator.EQUALS)
                                                                .value("USER")
                                                                .build()))
                                .build();

                when(contextManager.getContext(any(SubjectId.class), any(ResourceId.class), any())).thenReturn(context);
                when(policyRepository.getPolicies(any(), any(Action.class))).thenReturn(List.of(policy));
                Attribute attribute = Attribute.builder().id(new AttributeId("role")).source(Attribute.Source.SUBJECT).build();
                when(attributeProvider.getAttribute(new AttributeId("role"))).thenReturn(attribute);

                // when
                boolean result = evaluator.canAccess(new SubjectId("subject1"), new ResourceId("resource1"), Action.READ);

                // then
                assertThat(result).isFalse();
        }

        private Context createTestContext() {
                Map<AttributeId, AttributeValue> subjectAttributes = new HashMap<>();
                subjectAttributes.put(new AttributeId("role"), new AttributeValue("USER"));

                Map<AttributeId, AttributeValue> resourceAttributes = new HashMap<>();
                resourceAttributes.put(new AttributeId("type"), new AttributeValue("Document"));

                Map<AttributeId, AttributeValue> environmentAttributes = new HashMap<>();
                environmentAttributes.put(new AttributeId("timezone"), new AttributeValue("UTC"));

                return Context.builder()
                                .subject(Context.Subject.builder()
                                                .id(new SubjectId("subject1"))
                                                .type("User")
                                                .attributes(subjectAttributes)
                                                .build())
                                .resource(Context.Resource.builder()
                                                .id(new ResourceId("resource1"))
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

        private Context createTestContextWithRole(String role) {
                Map<AttributeId, AttributeValue> subjectAttributes = new HashMap<>();
                subjectAttributes.put(new AttributeId("role"), new AttributeValue(role));

                Map<AttributeId, AttributeValue> resourceAttributes = new HashMap<>();
                resourceAttributes.put(new AttributeId("type"), new AttributeValue("Document"));

                Map<AttributeId, AttributeValue> environmentAttributes = new HashMap<>();
                environmentAttributes.put(new AttributeId("timezone"), new AttributeValue("UTC"));

                return Context.builder()
                                .subject(Context.Subject.builder()
                                                .id(new SubjectId("subject1"))
                                                .type("User")
                                                .attributes(subjectAttributes)
                                                .build())
                                .resource(Context.Resource.builder()
                                                .id(new ResourceId("resource1"))
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