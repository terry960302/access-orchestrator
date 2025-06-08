package com.pandaterry.access_orchestrator;

import com.pandaterry.access_orchestrator.core.attribute.Attribute;
import com.pandaterry.access_orchestrator.core.attribute.AttributeProvider;
import com.pandaterry.access_orchestrator.core.context.Context;
import com.pandaterry.access_orchestrator.core.context.ContextManager;
import com.pandaterry.access_orchestrator.core.policy.*;
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
                                .id("role")
                                .name("Role")
                                .source(Attribute.Source.SUBJECT)
                                .dataType(Attribute.DataType.STRING)
                                .build();
                attributeProvider.setAttribute("role", roleAttribute);

                // 기본 필드 정책 설정
                FieldPolicy contentPolicy = FieldPolicy.builder()
                                .resourceType("Document")
                                .fieldName("content")
                                .conditions(List.of(
                                                Condition.builder()
                                                                .attributeId("role")
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
                                                                .attributeId("role")
                                                                .operator(Condition.Operator.EQUALS)
                                                                .value("ADMIN")
                                                                .build()))
                                .build();

                Map<String, Object> subjectAttributes = new HashMap<>();
                subjectAttributes.put("role", "ADMIN");

                Context context = Context.builder()
                                .subject(Context.Subject.builder()
                                                .id("subject1")
                                                .type("User")
                                                .attributes(subjectAttributes)
                                                .build())
                                .resource(Context.Resource.builder()
                                                .id("resource1")
                                                .type("Document")
                                                .attributes(new HashMap<>())
                                                .build())
                                .environment(Context.Environment.builder()
                                                .id("env1")
                                                .type("System")
                                                .attributes(new HashMap<>())
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
                Map<String, Object> subjectAttributes = new HashMap<>();
                subjectAttributes.put("role", "ADMIN");

                Context context = Context.builder()
                                .subject(Context.Subject.builder()
                                                .id("subject1")
                                                .type("User")
                                                .attributes(subjectAttributes)
                                                .build())
                                .resource(Context.Resource.builder()
                                                .id("resource1")
                                                .type("Document")
                                                .attributes(new HashMap<>())
                                                .build())
                                .environment(Context.Environment.builder()
                                                .id("env1")
                                                .type("System")
                                                .attributes(new HashMap<>())
                                                .build())
                                .build();

                contextManager.updateContext("subject1", context);

                // when
                boolean result = evaluator.canAccessField("subject1", "resource1", "content");

                // then
                assertThat(result).isTrue();
        }

        @Test
        @DisplayName("FieldPolicy의 조건이 불충족될 때, 해당 필드에 접근이 거부되어야 한다")
        void canAccessField_WithInvalidPolicyAndContext_ShouldDenyAccess() {
                // given
                Map<String, Object> subjectAttributes = new HashMap<>();
                subjectAttributes.put("role", "USER");

                Context context = Context.builder()
                                .subject(Context.Subject.builder()
                                                .id("subject1")
                                                .type("User")
                                                .attributes(subjectAttributes)
                                                .build())
                                .resource(Context.Resource.builder()
                                                .id("resource1")
                                                .type("Document")
                                                .attributes(new HashMap<>())
                                                .build())
                                .environment(Context.Environment.builder()
                                                .id("env1")
                                                .type("System")
                                                .attributes(new HashMap<>())
                                                .build())
                                .build();

                contextManager.updateContext("subject1", context);

                // when
                boolean result = evaluator.canAccessField("subject1", "resource1", "content");

                // then
                assertThat(result).isFalse();
        }
}