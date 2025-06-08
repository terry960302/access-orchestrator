package com.pandaterry.access_orchestrator.core.resource;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/approval-requests")
@RequiredArgsConstructor
public class ApprovalRequestController {
    private final Map<String, ApprovalRequest> approvalRequests = new ConcurrentHashMap<>();

    @PostMapping
    public ResponseEntity<ApprovalRequest> createApprovalRequest(@RequestBody ApprovalRequest request) {
        approvalRequests.put(request.getId(), request);
        return ResponseEntity.ok(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApprovalRequest> getApprovalRequest(@PathVariable String id) {
        ApprovalRequest request = approvalRequests.get(id);
        return request != null ? ResponseEntity.ok(request) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<ApprovalRequest>> getAllApprovalRequests() {
        return ResponseEntity.ok(List.copyOf(approvalRequests.values()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApprovalRequest> updateApprovalRequest(@PathVariable String id,
            @RequestBody ApprovalRequest request) {
        if (!approvalRequests.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        approvalRequests.put(id, request);
        return ResponseEntity.ok(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApprovalRequest(@PathVariable String id) {
        approvalRequests.remove(id);
        return ResponseEntity.ok().build();
    }
}