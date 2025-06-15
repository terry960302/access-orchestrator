package com.pandaterry.access_orchestrator.infrastructure.web;

import com.pandaterry.access_orchestrator.core.resource.Comment;
import com.pandaterry.access_orchestrator.core.resource.CommentId;
import com.pandaterry.access_orchestrator.core.resource.ResourceId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pandaterry.access_orchestrator.core.resource.service.CommentService;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody Comment comment) {
        return ResponseEntity.ok(commentService.create(comment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getComment(@PathVariable String id) {
        Comment comment = commentService.get(new CommentId(id));
        return comment != null ? ResponseEntity.ok(comment) : ResponseEntity.notFound().build();
    }

    @GetMapping("/resource/{resourceId}")
    public ResponseEntity<List<Comment>> getCommentsByResource(@PathVariable String resourceId) {
        ResourceId rid = new ResourceId(resourceId);
        List<Comment> resourceComments = commentService.getAll().stream()
                .filter(comment -> comment.getResourceId().equals(rid))
                .collect(Collectors.toList());
        return ResponseEntity.ok(resourceComments);
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getAllComments() {
        return ResponseEntity.ok(commentService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable String id, @RequestBody Comment comment) {
        CommentId commentId = new CommentId(id);
        if (commentService.get(commentId) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(commentService.update(commentId, comment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable String id) {
        commentService.delete(new CommentId(id));
        return ResponseEntity.ok().build();
    }
}