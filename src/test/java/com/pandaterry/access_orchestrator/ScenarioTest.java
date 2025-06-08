package com.pandaterry.access_orchestrator;

import com.pandaterry.access_orchestrator.core.attribute.Attribute;
import com.pandaterry.access_orchestrator.core.attribute.DefaultAttributeProvider;
import com.pandaterry.access_orchestrator.core.context.Context;
import com.pandaterry.access_orchestrator.core.context.DefaultContextManager;
import com.pandaterry.access_orchestrator.core.policy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ScenarioTest {
        @Autowired
        private DefaultPolicyEvaluator evaluator;
        @Autowired
        private DefaultContextManager contextManager;
        @Autowired
        private DefaultAttributeProvider attributeProvider;
        @Autowired
        private DefaultFieldPolicyManager fieldPolicyManager;

        @BeforeEach
        void setUp() {
                // 기본 속성 설정
                setupAttributes();
                // 필드 정책 설정
                setupFieldPolicies();
        }

        private void setupAttributes() {
                // 역할 속성
                Attribute roleAttribute = Attribute.builder()
                                .id("role")
                                .name("Role")
                                .source(Attribute.Source.SUBJECT)
                                .dataType(Attribute.DataType.STRING)
                                .build();
                attributeProvider.setAttribute("role", roleAttribute);

                // 직책 속성
                Attribute titleAttribute = Attribute.builder()
                                .id("title")
                                .name("Title")
                                .source(Attribute.Source.SUBJECT)
                                .dataType(Attribute.DataType.STRING)
                                .build();
                attributeProvider.setAttribute("title", titleAttribute);

                // 도메인 속성
                Attribute domainAttribute = Attribute.builder()
                                .id("domain")
                                .name("Domain")
                                .source(Attribute.Source.SUBJECT)
                                .dataType(Attribute.DataType.STRING)
                                .build();
                attributeProvider.setAttribute("domain", domainAttribute);

                // 가시성 속성
                Attribute visibilityAttribute = Attribute.builder()
                                .id("visibility")
                                .name("Visibility")
                                .source(Attribute.Source.RESOURCE)
                                .dataType(Attribute.DataType.STRING)
                                .build();
                attributeProvider.setAttribute("visibility", visibilityAttribute);

                // 상태 속성
                Attribute stateAttribute = Attribute.builder()
                                .id("state")
                                .name("State")
                                .source(Attribute.Source.RESOURCE)
                                .dataType(Attribute.DataType.STRING)
                                .build();
                attributeProvider.setAttribute("state", stateAttribute);
        }

        private void setupFieldPolicies() {
                // 예산 필드 정책
                FieldPolicy budgetPolicy = FieldPolicy.builder()
                                .resourceType("Document")
                                .fieldName("budget")
                                .conditions(List.of(
                                                Condition.builder()
                                                                .attributeId("title")
                                                                .operator(Condition.Operator.EQUALS)
                                                                .value("PM")
                                                                .build(),
                                                Condition.builder()
                                                                .attributeId("domain")
                                                                .operator(Condition.Operator.EQUALS)
                                                                .value("FINANCE")
                                                                .build()))
                                .effect(Policy.Effect.ALLOW)
                                .build();
                fieldPolicyManager.addFieldPolicy(budgetPolicy);
                System.out.println("Budget policy effect: " + budgetPolicy.getEffect());

                // 댓글 가시성 정책
                FieldPolicy commentVisibilityPolicy = FieldPolicy.builder()
                                .resourceType("Comment")
                                .fieldName("content")
                                .conditions(List.of(
                                                Condition.builder()
                                                                .attributeId("visibility")
                                                                .operator(Condition.Operator.EQUALS)
                                                                .value("PRIVATE")
                                                                .build()))
                                .effect(Policy.Effect.ALLOW)
                                .build();
                fieldPolicyManager.addFieldPolicy(commentVisibilityPolicy);
        }

        @Test
        @DisplayName("PM 역할의 사용자가 Document의 budget 필드에 접근할 때, 모든 조건을 만족하면 접근이 허용되어야 한다")
        void scenario1_DocumentBudgetFieldAccess() {
                // given
                Map<String, Object> pmAttributes = new HashMap<>();
                pmAttributes.put("role", "PM");
                pmAttributes.put("title", "PM");
                pmAttributes.put("domain", "FINANCE");

                System.out.println("PM attributes: " + pmAttributes); // PM 속성 출력

                Map<String, Object> documentAttributes = new HashMap<>();
                documentAttributes.put("type", "Document");

                Context pmContext = Context.builder()
                                .subject(Context.Subject.builder()
                                                .id("pm1")
                                                .type("User")
                                                .attributes(pmAttributes)
                                                .build())
                                .resource(Context.Resource.builder()
                                                .id("doc1")
                                                .type("Document")
                                                .attributes(documentAttributes)
                                                .build())
                                .environment(Context.Environment.builder()
                                                .id("env1")
                                                .type("System")
                                                .attributes(new HashMap<>())
                                                .build())
                                .build();

                contextManager.updateContext("pm1", pmContext);

                // when
                boolean canAccessBudget = evaluator.canAccessField("pm1", "doc1", "budget");

                // then
                System.out.println("Can access budget: " + canAccessBudget); // 접근 가능 여부 출력
                assertThat(canAccessBudget).isTrue();
        }

        @Test
        @DisplayName("visibility가 PRIVATE인 댓글에 일반 사용자가 접근할 때, 접근이 거부되어야 한다")
        void scenario2_PrivateCommentAccess() {
                // given
                Map<String, Object> subjectAttributes = new HashMap<>();
                subjectAttributes.put("role", "USER");

                Map<String, Object> resourceAttributes = new HashMap<>();
                resourceAttributes.put("visibility", "PRIVATE");

                Context context = Context.builder()
                                .subject(Context.Subject.builder()
                                                .id("user1")
                                                .type("User")
                                                .attributes(subjectAttributes)
                                                .build())
                                .resource(Context.Resource.builder()
                                                .id("comment1")
                                                .type("Comment")
                                                .attributes(resourceAttributes)
                                                .build())
                                .environment(Context.Environment.builder()
                                                .id("env1")
                                                .type("System")
                                                .attributes(new HashMap<>())
                                                .build())
                                .build();

                contextManager.updateContext("user1", context);

                // when
                boolean canAccessComment = evaluator.canAccessField("user1", "comment1", "content");

                // then
                assertThat(canAccessComment).isFalse();
        }

        @Test
        @DisplayName("Document의 상태가 APPROVED일 때, 수정 정책이 거부되어야 한다")
        void scenario3_ApprovedDocumentAccess() {
                // given
                Policy policy = Policy.builder()
                                .effect(Policy.Effect.ALLOW)
                                .conditions(List.of(
                                                Condition.builder()
                                                                .attributeId("state")
                                                                .operator(Condition.Operator.NOT_EQUALS)
                                                                .value("APPROVED")
                                                                .build()))
                                .build();

                Map<String, Object> resourceAttributes = new HashMap<>();
                resourceAttributes.put("state", "APPROVED");

                Context context = Context.builder()
                                .subject(Context.Subject.builder()
                                                .id("user1")
                                                .type("User")
                                                .attributes(new HashMap<>())
                                                .build())
                                .resource(Context.Resource.builder()
                                                .id("doc1")
                                                .type("Document")
                                                .attributes(resourceAttributes)
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
                assertThat(result).isEqualTo(Policy.Effect.DENY);
        }
}