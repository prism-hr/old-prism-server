package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.domain.Comment;

@Service
public class CommentService {

	private final CommentDAO commentDAO;

	CommentService() {
		this(null);
	}

	@Autowired
	public CommentService(CommentDAO commentDAO) {
		this.commentDAO = commentDAO;
	}

	@Transactional
	public void save(Comment comment) {
		commentDAO.save(comment);
	}

	public Comment getReviewById(int id) {
		return commentDAO.get(id);
	}

}
