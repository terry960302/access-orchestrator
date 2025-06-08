package com.pandaterry.access_orchestrator;

import com.pandaterry.access_orchestrator.core.policy.DefaultFieldPolicyManager;
import com.pandaterry.access_orchestrator.core.policy.FieldPolicy;
import com.pandaterry.access_orchestrator.core.policy.Policy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultFieldPolicyManagerTest {
        private DefaultFieldPolicyManager manager;

        @BeforeEach
        void setUp() {
                manager = new DefaultFieldPolicyManager();
        }

        @Test
        @DisplayName("Document의 content 필드 정책을 추가하면, 해당 필드 정책이 정상적으로 저장되어야 한다")
        void addFieldPolicy_ShouldAddPolicy() {
                // given
                FieldPolicy policy = FieldPolicy.builder()
                                .resourceType("Document")
                                .fieldName("content")
                                .effect(Policy.Effect.ALLOW)
                                .build();

                // when
                manager.addFieldPolicy(policy);

                // then
                List<FieldPolicy> policies = manager.getFieldPolicies("Document");
                assertThat(policies).hasSize(1);
                assertThat(policies.get(0)).isEqualTo(policy);
        }

        @Test
        @DisplayName("Document의 content 필드 정책을 삭제하면, 해당 필드 정책이 리스트에서 제거되어야 한다")
        void removeFieldPolicy_ShouldRemovePolicy() {
                // given
                FieldPolicy policy = FieldPolicy.builder()
                                .resourceType("Document")
                                .fieldName("content")
                                .effect(Policy.Effect.ALLOW)
                                .build();
                manager.addFieldPolicy(policy);

                // when
                manager.removeFieldPolicy("Document", "content");

                // then
                List<FieldPolicy> policies = manager.getFieldPolicies("Document");
                assertThat(policies).isEmpty();
        }

        @Test
        @DisplayName("Document의 content 필드 정책을 조회하면, 해당 필드 정책이 반환되어야 한다")
        void getFieldPolicy_ShouldReturnPolicy() {
                // given
                FieldPolicy policy = FieldPolicy.builder()
                                .resourceType("Document")
                                .fieldName("content")
                                .effect(Policy.Effect.ALLOW)
                                .build();
                manager.addFieldPolicy(policy);

                // when
                FieldPolicy result = manager.getFieldPolicy("Document", "content");

                // then
                assertThat(result).isEqualTo(policy);
        }

        @Test
        @DisplayName("Document 타입에 여러 필드 정책을 추가하면, 해당 타입의 모든 필드 정책이 반환되어야 한다")
        void getFieldPolicies_ShouldReturnAllPoliciesForResourceType() {
                // given
                FieldPolicy policy1 = FieldPolicy.builder()
                                .resourceType("Document")
                                .fieldName("content")
                                .effect(Policy.Effect.ALLOW)
                                .build();
                FieldPolicy policy2 = FieldPolicy.builder()
                                .resourceType("Document")
                                .fieldName("title")
                                .effect(Policy.Effect.ALLOW)
                                .build();
                manager.addFieldPolicy(policy1);
                manager.addFieldPolicy(policy2);

                // when
                List<FieldPolicy> policies = manager.getFieldPolicies("Document");

                // then
                assertThat(policies).hasSize(2);
                assertThat(policies).containsExactlyInAnyOrder(policy1, policy2);
        }

        @Test
        @DisplayName("여러 타입의 필드 정책을 추가하면, 전체 정책 맵에서 각 타입별로 올바르게 조회되어야 한다")
        void getAllFieldPolicies_ShouldReturnAllPolicies() {
                // given
                FieldPolicy policy1 = FieldPolicy.builder()
                                .resourceType("Document")
                                .fieldName("content")
                                .effect(Policy.Effect.ALLOW)
                                .build();
                FieldPolicy policy2 = FieldPolicy.builder()
                                .resourceType("Asset")
                                .fieldName("url")
                                .effect(Policy.Effect.ALLOW)
                                .build();
                manager.addFieldPolicy(policy1);
                manager.addFieldPolicy(policy2);

                // when
                Map<String, List<FieldPolicy>> allPolicies = manager.getAllFieldPolicies();

                // then
                assertThat(allPolicies).hasSize(2);
                assertThat(allPolicies.get("Document")).containsExactly(policy1);
                assertThat(allPolicies.get("Asset")).containsExactly(policy2);
        }

        @Test
        void clearFieldPolicies_ShouldRemoveAllPolicies() {
                // given
                FieldPolicy policy1 = FieldPolicy.builder()
                                .resourceType("Document")
                                .fieldName("content")
                                .effect(Policy.Effect.ALLOW)
                                .build();
                FieldPolicy policy2 = FieldPolicy.builder()
                                .resourceType("Asset")
                                .fieldName("url")
                                .effect(Policy.Effect.ALLOW)
                                .build();
                manager.addFieldPolicy(policy1);
                manager.addFieldPolicy(policy2);

                // when
                manager.clearFieldPolicies();

                // then
                Map<String, List<FieldPolicy>> allPolicies = manager.getAllFieldPolicies();
                assertThat(allPolicies).isEmpty();
        }
}