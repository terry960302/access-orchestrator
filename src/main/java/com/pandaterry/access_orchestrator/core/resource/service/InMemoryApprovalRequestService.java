package com.pandaterry.access_orchestrator.core.resource.service;

import com.pandaterry.access_orchestrator.core.resource.ApprovalRequestId;
import com.pandaterry.access_orchestrator.core.resource.ApprovalRequest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
public class InMemoryApprovalRequestService implements ApprovalRequestService {
    private final Map<ApprovalRequestId, ApprovalRequest> approvalRequests = new ConcurrentHashMap<>();

    @Override
    public ApprovalRequest create(ApprovalRequest request) {
        approvalRequests.put(request.getId(), request);
        return request;
    }

    @Override
    public ApprovalRequest get(ApprovalRequestId id) {
        return approvalRequests.get(id);
    }

    @Override
    public List<ApprovalRequest> getAll() {
        return List.copyOf(approvalRequests.values());
    }

    @Override
    public ApprovalRequest update(ApprovalRequestId id, ApprovalRequest request) {
        approvalRequests.put(id, request);
        return request;
    }

    @Override
    public void delete(ApprovalRequestId id) {
        approvalRequests.remove(id);
    }
}
