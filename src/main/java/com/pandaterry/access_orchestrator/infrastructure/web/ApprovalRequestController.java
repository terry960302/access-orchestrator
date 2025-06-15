package com.pandaterry.access_orchestrator.infrastructure.web;

import com.pandaterry.access_orchestrator.core.resource.ApprovalRequest;
import com.pandaterry.access_orchestrator.core.resource.ApprovalRequestId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pandaterry.access_orchestrator.core.resource.service.ApprovalRequestService;
import java.util.List;

@RestController
@RequestMapping("/api/approval-requests")
@RequiredArgsConstructor
public class ApprovalRequestController {
    private final ApprovalRequestService approvalRequestService;

    @PostMapping
    public ResponseEntity<ApprovalRequest> createApprovalRequest(@RequestBody ApprovalRequest request) {
        return ResponseEntity.ok(approvalRequestService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApprovalRequest> getApprovalRequest(@PathVariable String id) {
        ApprovalRequest request = approvalRequestService.get(new ApprovalRequestId(id));
        return request != null ? ResponseEntity.ok(request) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<ApprovalRequest>> getAllApprovalRequests() {
        return ResponseEntity.ok(approvalRequestService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApprovalRequest> updateApprovalRequest(@PathVariable String id,
            @RequestBody ApprovalRequest request) {
        ApprovalRequestId requestId = new ApprovalRequestId(id);
        if (approvalRequestService.get(requestId) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(approvalRequestService.update(requestId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApprovalRequest(@PathVariable String id) {
        approvalRequestService.delete(new ApprovalRequestId(id));
        return ResponseEntity.ok().build();
    }
}