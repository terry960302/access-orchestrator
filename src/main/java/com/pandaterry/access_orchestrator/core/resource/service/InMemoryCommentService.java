package com.pandaterry.access_orchestrator.core.resource.service;

import com.pandaterry.access_orchestrator.core.resource.CommentId;
import com.pandaterry.access_orchestrator.core.resource.Comment;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
public class InMemoryCommentService implements CommentService {
    private final Map<CommentId, Comment> comments = new ConcurrentHashMap<>();

    @Override
    public Comment create(Comment comment) {
        comments.put(comment.getId(), comment);
        return comment;
    }

    @Override
    public Comment get(CommentId id) {
        return comments.get(id);
    }

    @Override
    public List<Comment> getAll() {
        return List.copyOf(comments.values());
    }

    @Override
    public Comment update(CommentId id, Comment comment) {
        comments.put(id, comment);
        return comment;
    }

    @Override
    public void delete(CommentId id) {
        comments.remove(id);
    }
}
