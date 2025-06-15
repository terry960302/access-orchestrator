package com.pandaterry.access_orchestrator;

import com.pandaterry.access_orchestrator.core.attribute.Attribute;
import com.pandaterry.access_orchestrator.core.attribute.DefaultAttributeProvider;
import com.pandaterry.access_orchestrator.core.attribute.AttributeId;
import com.pandaterry.access_orchestrator.core.attribute.AttributeValue;
import com.pandaterry.access_orchestrator.core.context.Context;
import com.pandaterry.access_orchestrator.core.context.DefaultContextManager;
import com.pandaterry.access_orchestrator.core.policy.*;
import com.pandaterry.access_orchestrator.core.resource.FieldName;
import com.pandaterry.access_orchestrator.core.resource.SubjectId;
import com.pandaterry.access_orchestrator.core.resource.ResourceId;
import com.pandaterry.access_orchestrator.core.resource.Action;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
                                .id(new AttributeId("role"))
                                .name("Role")
                                .source(Attribute.Source.SUBJECT)
                                .dataType(Attribute.DataType.STRING)
                                .build();
                attributeProvider.setAttribute(new AttributeId("role"), roleAttribute);

                // 직책 속성
                Attribute titleAttribute = Attribute.builder()
                                .id(new AttributeId("title"))
                                .name("Title")
                                .source(Attribute.Source.SUBJECT)
                                .dataType(Attribute.DataType.STRING)
                                .build();
                attributeProvider.setAttribute(new AttributeId("title"), titleAttribute);

                // 도메인 속성
                Attribute domainAttribute = Attribute.builder()
                                .id(new AttributeId("domain"))
                                .name("Domain")
                                .source(Attribute.Source.SUBJECT)
                                .dataType(Attribute.DataType.STRING)
                                .build();
                attributeProvider.setAttribute(new AttributeId("domain"), domainAttribute);

                // 가시성 속성
                Attribute visibilityAttribute = Attribute.builder()
                                .id(new AttributeId("visibility"))
                                .name("Visibility")
                                .source(Attribute.Source.RESOURCE)
                                .dataType(Attribute.DataType.STRING)
                                .build();
                attributeProvider.setAttribute(new AttributeId("visibility"), visibilityAttribute);

                // 상태 속성
                Attribute stateAttribute = Attribute.builder()
                                .id(new AttributeId("state"))
                                .name("State")
                                .source(Attribute.Source.RESOURCE)
                                .dataType(Attribute.DataType.STRING)
                                .build();
                attributeProvider.setAttribute(new AttributeId("state"), stateAttribute);
        }

        private void setupFieldPolicies() {
                // 예산 필드 정책
                FieldPolicy budgetPolicy = FieldPolicy.builder()
                                .resourceType("Document")
                                .fieldName(new FieldName("budget"))
                                .conditions(List.of(
                                                Condition.builder()
                                                                .attributeId(new AttributeId("title"))
                                                                .operator(Condition.Operator.EQUALS)
                                                                .value("PM")
                                                                .build(),
                                                Condition.builder()
                                                                .attributeId(new AttributeId("domain"))
                                                                .operator(Condition.Operator.EQUALS)
                                                                .value("FINANCE")
                                                                .build()))
                                .effect(Policy.Effect.ALLOW)
                                .build();
                fieldPolicyManager.addFieldPolicy(budgetPolicy);

                // 댓글 가시성 정책
                FieldPolicy commentVisibilityPolicy = FieldPolicy.builder()
                                .resourceType("Comment")
                                .fieldName(new FieldName("content"))
                                .conditions(List.of(
                                                Condition.builder()
                                                                .attributeId(new AttributeId("visibility"))
                                                                .operator(Condition.Operator.EQUALS)
                                                                .value("PRIVATE")
                                                                .build()))
                                .effect(Policy.Effect.DENY)
                                .build();
                fieldPolicyManager.addFieldPolicy(commentVisibilityPolicy);
        }

        @Test
        @DisplayName("PM 역할의 사용자가 Document의 budget 필드에 접근할 때, 모든 조건을 만족하면 접근이 허용되어야 한다")
        void scenario1_DocumentBudgetFieldAccess() {
                // given
                Map<AttributeId, AttributeValue> pmAttributes = new HashMap<>();
                pmAttributes.put(new AttributeId("role"), new AttributeValue("PM"));
                pmAttributes.put(new AttributeId("title"), new AttributeValue("PM"));
                pmAttributes.put(new AttributeId("domain"), new AttributeValue("FINANCE"));


                Map<AttributeId, AttributeValue> documentAttributes = new HashMap<>();
                documentAttributes.put(new AttributeId("type"), new AttributeValue("Document"));

                Context pmContext = Context.builder()
                                .subject(Context.Subject.builder()
                                                .id(new SubjectId("pm1"))
                                                .type("User")
                                                .attributes(pmAttributes)
                                                .build())
                                .resource(Context.Resource.builder()
                                                .id(new ResourceId("doc1"))
                                                .type("Document")
                                                .attributes(documentAttributes)
                                                .build())
                                .environment(Context.Environment.builder()
                                                .id("env1")
                                                .type("System")
                                                .attributes(new HashMap<AttributeId, AttributeValue>())
                                                .build())
                                .build();

                contextManager.updateContext(new SubjectId("pm1"), new ResourceId("doc1"), Action.READ, pmContext);

                // when
                boolean canAccessBudget = evaluator.canAccessField(new SubjectId("pm1"), new ResourceId("doc1"), new FieldName("budget"));

                // then
                assertThat(canAccessBudget).isTrue();
        }

        @Test
        @DisplayName("visibility가 PRIVATE인 댓글에 일반 사용자가 접근할 때, 접근이 거부되어야 한다")
        void scenario2_PrivateCommentAccess() {
                // given
                Map<AttributeId, AttributeValue> subjectAttributes = new HashMap<>();
                subjectAttributes.put(new AttributeId("role"), new AttributeValue("USER"));

                Map<AttributeId, AttributeValue> resourceAttributes = new HashMap<>();
                resourceAttributes.put(new AttributeId("visibility"), new AttributeValue("PRIVATE"));

                Context context = Context.builder()
                                .subject(Context.Subject.builder()
                                                .id(new SubjectId("user1"))
                                                .type("User")
                                                .attributes(subjectAttributes)
                                                .build())
                                .resource(Context.Resource.builder()
                                                .id(new ResourceId("comment1"))
                                                .type("Comment")
                                                .attributes(resourceAttributes)
                                                .build())
                                .environment(Context.Environment.builder()
                                                .id("env1")
                                                .type("System")
                                                .attributes(new HashMap<AttributeId, AttributeValue>())
                                                .build())
                                .build();

                contextManager.updateContext(new SubjectId("user1"), new ResourceId("comment1"), Action.READ, context);

                // when
                boolean canAccessComment = evaluator.canAccessField(new SubjectId("user1"), new ResourceId("comment1"), new FieldName("content"));

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
                                                                .attributeId(new AttributeId("state"))
                                                                .operator(Condition.Operator.NOT_EQUALS)
                                                                .value("APPROVED")
                                                                .build()))
                                .build();

                Map<AttributeId, AttributeValue> resourceAttributes = new HashMap<>();
                resourceAttributes.put(new AttributeId("state"), new AttributeValue("APPROVED"));

                Context context = Context.builder()
                                .subject(Context.Subject.builder()
                                                .id(new SubjectId("user1"))
                                                .type("User")
                                                .attributes(new HashMap<AttributeId, AttributeValue>())
                                                .build())
                                .resource(Context.Resource.builder()
                                                .id(new ResourceId("doc1"))
                                                .type("Document")
                                                .attributes(resourceAttributes)
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
                assertThat(result).isEqualTo(Policy.Effect.DENY);
        }
}