package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.ReviewComment;

@Service
@Transactional
public class CommentService {

    private final CommentDAO commentDAO;

    public CommentService() {
        this(null);
    }

    @Autowired
    public CommentService(CommentDAO commentDAO) {
        this.commentDAO = commentDAO;
    }

    public void save(Comment comment) {
        commentDAO.save(comment);
    }

    public Comment getReviewById(int id) {
        return commentDAO.get(id);
    }

    public List<Comment> getAllComments() {
        return commentDAO.getAllComments();
    }

    public Comment getNewGenericComment() {
        return new Comment();
    }

    public ReviewComment getNewReviewComment() {
        return new ReviewComment();
    }
    
}