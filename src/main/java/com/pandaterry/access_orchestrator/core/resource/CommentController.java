package com.pandaterry.access_orchestrator.core.resource;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final Map<String, Comment> comments = new ConcurrentHashMap<>();

    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody Comment comment) {
        comments.put(comment.getId(), comment);
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getComment(@PathVariable String id) {
        Comment comment = comments.get(id);
        return comment != null ? ResponseEntity.ok(comment) : ResponseEntity.notFound().build();
    }

    @GetMapping("/resource/{resourceId}")
    public ResponseEntity<List<Comment>> getCommentsByResource(@PathVariable String resourceId) {
        List<Comment> resourceComments = comments.values().stream()
                .filter(comment -> comment.getResourceId().equals(resourceId))
                .collect(Collectors.toList());
        return ResponseEntity.ok(resourceComments);
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getAllComments() {
        return ResponseEntity.ok(List.copyOf(comments.values()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable String id, @RequestBody Comment comment) {
        if (!comments.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        comments.put(id, comment);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable String id) {
        comments.remove(id);
        return ResponseEntity.ok().build();
    }
}