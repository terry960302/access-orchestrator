package com.pandaterry.access_orchestrator;

import com.pandaterry.access_orchestrator.core.attribute.Attribute;
import com.pandaterry.access_orchestrator.core.attribute.AttributeProvider;
import com.pandaterry.access_orchestrator.core.context.Context;
import com.pandaterry.access_orchestrator.core.context.ContextManager;
import com.pandaterry.access_orchestrator.core.policy.*;
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

        private DefaultPolicyEvaluator evaluator;

        @BeforeEach
        void setUp() {
                evaluator = new DefaultPolicyEvaluator(contextManager, attributeProvider, fieldPolicyManager);
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
                                                                .attributeId("role")
                                                                .operator(Condition.Operator.EQUALS)
                                                                .value("ADMIN")
                                                                .build()))
                                .build();
                Context context = createTestContextWithRole("ADMIN");
                Attribute attribute = Attribute.builder()
                                .id("role")
                                .source(Attribute.Source.SUBJECT)
                                .build();

                when(attributeProvider.getAttribute("role")).thenReturn(attribute);

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
                                                                .attributeId("role")
                                                                .operator(Condition.Operator.EQUALS)
                                                                .value("ADMIN")
                                                                .build()))
                                .build();
                Context context = createTestContext();
                Attribute attribute = Attribute.builder()
                                .id("role")
                                .source(Attribute.Source.SUBJECT)
                                .build();

                when(attributeProvider.getAttribute("role")).thenReturn(attribute);

                // when
                Policy.Effect result = evaluator.evaluate(policy, context);

                // then
                assertThat(result).isEqualTo(Policy.Effect.DENY);
        }

        @Test
        void canAccessField_WhenNoPolicy_ShouldReturnTrue() {
                // given
                Context context = createTestContext();
                when(contextManager.getContext(any(), any(), any())).thenReturn(context);
                when(fieldPolicyManager.getFieldPolicy(any(), any())).thenReturn(null);

                // when
                boolean result = evaluator.canAccessField("subject1", "resource1", "field1");

                // then
                assertThat(result).isTrue();
        }

        @Test
        void canAccessField_WhenPolicyExistsAndAccessible_ShouldReturnTrue() {
                // given
                Context context = createTestContext();
                FieldPolicy fieldPolicy = FieldPolicy.builder()
                                .resourceType("Document")
                                .fieldName("field1")
                                .accessible(true)
                                .build();

                when(contextManager.getContext(any(), any(), any())).thenReturn(context);
                when(fieldPolicyManager.getFieldPolicy(any(), any())).thenReturn(fieldPolicy);

                // when
                boolean result = evaluator.canAccessField("subject1", "resource1", "field1");

                // then
                assertThat(result).isTrue();
        }

        @Test
        void canAccessField_WhenPolicyExistsAndNotAccessible_ShouldReturnFalse() {
                // given
                Context context = createTestContext();
                FieldPolicy fieldPolicy = FieldPolicy.builder()
                                .resourceType("Document")
                                .fieldName("field1")
                                .accessible(false)
                                .build();

                when(contextManager.getContext(any(), any(), any())).thenReturn(context);
                when(fieldPolicyManager.getFieldPolicy(any(), any())).thenReturn(fieldPolicy);

                // when
                boolean result = evaluator.canAccessField("subject1", "resource1", "field1");

                // then
                assertThat(result).isFalse();
        }

        private Context createTestContext() {
                Map<String, Object> subjectAttributes = new HashMap<>();
                subjectAttributes.put("role", "USER");

                Map<String, Object> resourceAttributes = new HashMap<>();
                resourceAttributes.put("type", "Document");

                Map<String, Object> environmentAttributes = new HashMap<>();
                environmentAttributes.put("timezone", "UTC");

                return Context.builder()
                                .subject(Context.Subject.builder()
                                                .id("subject1")
                                                .type("User")
                                                .attributes(subjectAttributes)
                                                .build())
                                .resource(Context.Resource.builder()
                                                .id("resource1")
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
                Map<String, Object> subjectAttributes = new HashMap<>();
                subjectAttributes.put("role", role);

                Map<String, Object> resourceAttributes = new HashMap<>();
                resourceAttributes.put("type", "Document");

                Map<String, Object> environmentAttributes = new HashMap<>();
                environmentAttributes.put("timezone", "UTC");

                return Context.builder()
                                .subject(Context.Subject.builder()
                                                .id("subject1")
                                                .type("User")
                                                .attributes(subjectAttributes)
                                                .build())
                                .resource(Context.Resource.builder()
                                                .id("resource1")
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