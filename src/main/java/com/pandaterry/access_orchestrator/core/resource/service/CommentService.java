package com.pandaterry.access_orchestrator.core.resource.service;

import com.pandaterry.access_orchestrator.core.resource.CommentId;
import com.pandaterry.access_orchestrator.core.resource.Comment;

import java.util.List;

public interface CommentService {
    Comment create(Comment comment);

    Comment get(CommentId id);

    List<Comment> getAll();

    Comment update(CommentId id, Comment comment);

    void delete(CommentId id);
}
