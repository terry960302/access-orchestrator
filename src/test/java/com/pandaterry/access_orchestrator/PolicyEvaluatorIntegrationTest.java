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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
//@Import(TestConfig.class)
class PolicyEvaluatorIntegrationTest {
        @Autowired
        private PolicyEvaluator evaluator;
        @Autowired
        private ContextManager contextManager;
        @Autowired
        private AttributeProvider attributeProvider;
        @Autowired
        private FieldPolicyManager fieldPolicyManager;

        @BeforeEach
        void setUp() {
                // 기본 속성 설정
                Attribute roleAttribute = Attribute.builder()
                                .id(new AttributeId("role"))
                                .name("Role")
                                .source(Attribute.Source.SUBJECT)
                                .dataType(Attribute.DataType.STRING)
                                .build();
                attributeProvider.setAttribute(new AttributeId("role"), roleAttribute);

                // 기본 필드 정책 설정
                FieldPolicy contentPolicy = FieldPolicy.builder()
                                .resourceType("Document")
                                .fieldName(new FieldName("content"))
                                .conditions(List.of(
                                                Condition.builder()
                                                                .attributeId(new AttributeId("role"))
                                                                .operator(Condition.Operator.EQUALS)
                                                                .value("ADMIN")
                                                                .build()))
                                .effect(Policy.Effect.ALLOW)
                                .build();
                fieldPolicyManager.addFieldPolicy(contentPolicy);
        }

        @Test
        @DisplayName("Policy의 조건이 모두 만족될 때, 평가 결과가 ALLOW가 되어야 한다")
        void evaluate_WithValidPolicyAndContext_ShouldAllowAccess() {
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

                Map<AttributeId, AttributeValue> subjectAttributes = new HashMap<>();
                subjectAttributes.put(new AttributeId("role"), new AttributeValue("ADMIN"));

                Context context = Context.builder()
                                .subject(Context.Subject.builder()
                                                .id(new SubjectId("subject1"))
                                                .type("User")
                                                .attributes(subjectAttributes)
                                                .build())
                                .resource(Context.Resource.builder()
                                                .id(new ResourceId("resource1"))
                                                .type("Document")
                                                .attributes(new HashMap<AttributeId, AttributeValue>())
                                                .build())
                                .environment(Context.Environment.builder()
                                                .id("env1")
                                                .type("System")
                                                .attributes(new HashMap<AttributeId, AttributeValue>())
                                                .build())
                                .build();

                // when
                Policy.Effect result = evaluator.evaluate(policy, context);

                // then
                assertThat(result).isEqualTo(Policy.Effect.ALLOW);
        }

        @Test
        @DisplayName("FieldPolicy의 조건이 모두 만족될 때, 해당 필드에 접근이 허용되어야 한다")
        void canAccessField_WithValidPolicyAndContext_ShouldAllowAccess() {
                // given
                Map<AttributeId, AttributeValue> subjectAttributes = new HashMap<>();
                subjectAttributes.put(new AttributeId("role"), new AttributeValue("ADMIN"));

                Context context = Context.builder()
                                .subject(Context.Subject.builder()
                                                .id(new SubjectId("subject1"))
                                                .type("User")
                                                .attributes(subjectAttributes)
                                                .build())
                                .resource(Context.Resource.builder()
                                                .id(new ResourceId("resource1"))
                                                .type("Document")
                                                .attributes(new HashMap<AttributeId, AttributeValue>())
                                                .build())
                                .environment(Context.Environment.builder()
                                                .id("env1")
                                                .type("System")
                                                .attributes(new HashMap<AttributeId, AttributeValue>())
                                                .build())
                                .build();

                contextManager.updateContext(new SubjectId("subject1"), new ResourceId("resource1"), Action.READ, context);

                // when
                boolean result = evaluator.canAccessField(new SubjectId("subject1"), new ResourceId("resource1"), new FieldName("content"));

                // then
                assertThat(result).isTrue();
        }

        @Test
        @DisplayName("FieldPolicy의 조건이 불충족될 때, 해당 필드에 접근이 거부되어야 한다")
        void canAccessField_WithInvalidPolicyAndContext_ShouldDenyAccess() {
                // given
                Map<AttributeId, AttributeValue> subjectAttributes = new HashMap<>();
                subjectAttributes.put(new AttributeId("role"), new AttributeValue("USER"));

                Context context = Context.builder()
                                .subject(Context.Subject.builder()
                                                .id(new SubjectId("subject1"))
                                                .type("User")
                                                .attributes(subjectAttributes)
                                                .build())
                                .resource(Context.Resource.builder()
                                                .id(new ResourceId("resource1"))
                                                .type("Document")
                                                .attributes(new HashMap<AttributeId, AttributeValue>())
                                                .build())
                                .environment(Context.Environment.builder()
                                                .id("env1")
                                                .type("System")
                                                .attributes(new HashMap<AttributeId, AttributeValue>())
                                                .build())
                                .build();

                contextManager.updateContext(new SubjectId("subject1"), new ResourceId("resource1"), Action.READ, context);

                // when
                boolean result = evaluator.canAccessField(new SubjectId("subject1"), new ResourceId("resource1"), new FieldName("content"));

                // then
                assertThat(result).isFalse();
        }
}