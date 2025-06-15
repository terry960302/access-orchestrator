package com.pandaterry.access_orchestrator;

import com.pandaterry.access_orchestrator.core.policy.DefaultFieldPolicyManager;
import com.pandaterry.access_orchestrator.core.policy.FieldPolicy;
import com.pandaterry.access_orchestrator.core.policy.Policy;
import com.pandaterry.access_orchestrator.core.resource.FieldName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
                                .fieldName(new FieldName("content"))
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
                                .fieldName(new FieldName("content"))
                                .effect(Policy.Effect.ALLOW)
                                .build();
                manager.addFieldPolicy(policy);

                // when
                manager.removeFieldPolicy("Document", new FieldName("content"));

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
                                .fieldName(new FieldName("content"))
                                .effect(Policy.Effect.ALLOW)
                                .build();
                manager.addFieldPolicy(policy);

                // when
                FieldPolicy result = manager.getFieldPolicy("Document", new FieldName("content"));

                // then
                assertThat(result).isEqualTo(policy);
        }

        @Test
        @DisplayName("Document 타입에 여러 필드 정책을 추가하면, 해당 타입의 모든 필드 정책이 반환되어야 한다")
        void getFieldPolicies_ShouldReturnAllPoliciesForResourceType() {
                // given
                FieldPolicy policy1 = FieldPolicy.builder()
                                .resourceType("Document")
                                .fieldName(new FieldName("content"))
                                .effect(Policy.Effect.ALLOW)
                                .build();
                FieldPolicy policy2 = FieldPolicy.builder()
                                .resourceType("Document")
                                .fieldName(new FieldName("title"))
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
                                .fieldName(new FieldName("content"))
                                .effect(Policy.Effect.ALLOW)
                                .build();
                FieldPolicy policy2 = FieldPolicy.builder()
                                .resourceType("Asset")
                                .fieldName(new FieldName("url"))
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
                                .fieldName(new FieldName("content"))
                                .effect(Policy.Effect.ALLOW)
                                .build();
                FieldPolicy policy2 = FieldPolicy.builder()
                                .resourceType("Asset")
                                .fieldName(new FieldName("url"))
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

        @Test
        @DisplayName("여러 스레드가 동시에 정책을 추가/삭제해도 예외가 발생하지 않아야 한다")
        void concurrentModification_ShouldNotThrowException() throws Exception {
                int threadCount = 10;
                ExecutorService executor = Executors.newFixedThreadPool(threadCount);
                List<Callable<Void>> tasks = new ArrayList<>();

                for (int i = 0; i < threadCount; i++) {
                        final int idx = i;
                        tasks.add(() -> {
                                FieldPolicy policy = FieldPolicy.builder()
                                                .resourceType("Document")
                                                .fieldName(new FieldName("field" + idx))
                                                .effect(Policy.Effect.ALLOW)
                                                .build();
                                manager.addFieldPolicy(policy);
                                manager.removeFieldPolicy("Document", new FieldName("field" + idx));
                                return null;
                        });
                }

                List<Future<Void>> futures = executor.invokeAll(tasks);
                for (Future<Void> f : futures) {
                        f.get();
                }
                executor.shutdown();
                executor.awaitTermination(5, TimeUnit.SECONDS);
        }
}