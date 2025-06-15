package com.pandaterry.access_orchestrator.infrastructure;

import com.pandaterry.access_orchestrator.core.attribute.AttributeProvider;
import com.pandaterry.access_orchestrator.core.attribute.DefaultAttributeProvider;
import com.pandaterry.access_orchestrator.core.context.ContextManager;
import com.pandaterry.access_orchestrator.core.context.DefaultContextManager;
import com.pandaterry.access_orchestrator.core.policy.DefaultFieldPolicyManager;
import com.pandaterry.access_orchestrator.core.policy.DefaultPolicyEvaluator;
import com.pandaterry.access_orchestrator.core.policy.DefaultPolicyRepository;
import com.pandaterry.access_orchestrator.core.policy.FieldPolicyManager;
import com.pandaterry.access_orchestrator.core.policy.PolicyEvaluator;
import com.pandaterry.access_orchestrator.core.policy.PolicyRepository;
import com.pandaterry.access_orchestrator.core.resource.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfrastructureConfig {
    @Bean
    public AttributeProvider attributeProvider() {
        return new DefaultAttributeProvider();
    }

    @Bean
    public ContextManager contextManager() {
        return new DefaultContextManager();
    }

    @Bean
    public FieldPolicyManager fieldPolicyManager() {
        return new DefaultFieldPolicyManager();
    }

    @Bean
    public PolicyRepository policyRepository() {
        return new DefaultPolicyRepository();
    }

    @Bean
    public PolicyEvaluator policyEvaluator(ContextManager contextManager,
                                           AttributeProvider attributeProvider,
                                           FieldPolicyManager fieldPolicyManager,
                                           PolicyRepository policyRepository) {
        return new DefaultPolicyEvaluator(contextManager, attributeProvider, fieldPolicyManager, policyRepository);
    }

    @Bean
    public DocumentService documentService() {
        return new InMemoryDocumentService();
    }

    @Bean
    public AssetService assetService() {
        return new InMemoryAssetService();
    }

    @Bean
    public ApprovalRequestService approvalRequestService() {
        return new InMemoryApprovalRequestService();
    }

    @Bean
    public CommentService commentService() {
        return new InMemoryCommentService();
    }
}
