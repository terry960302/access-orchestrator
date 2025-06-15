package com.pandaterry.access_orchestrator.core.resource.service;

import com.pandaterry.access_orchestrator.core.resource.ApprovalRequestId;
import com.pandaterry.access_orchestrator.core.resource.ApprovalRequest;

import java.util.List;

public interface ApprovalRequestService {
    ApprovalRequest create(ApprovalRequest request);

    ApprovalRequest get(ApprovalRequestId id);

    List<ApprovalRequest> getAll();

    ApprovalRequest update(ApprovalRequestId id, ApprovalRequest request);

    void delete(ApprovalRequestId id);
}
