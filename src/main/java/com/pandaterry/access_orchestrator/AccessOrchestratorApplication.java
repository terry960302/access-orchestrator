package com.pandaterry.access_orchestrator;

import com.pandaterry.access_orchestrator.core.attribute.DefaultAttributeProvider;
import com.pandaterry.access_orchestrator.core.context.DefaultContextManager;
import com.pandaterry.access_orchestrator.core.policy.DefaultFieldPolicyManager;
import com.pandaterry.access_orchestrator.core.policy.DefaultPolicyEvaluator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AccessOrchestratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccessOrchestratorApplication.class, args);
	}
}
