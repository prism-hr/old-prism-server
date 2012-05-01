package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;

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

	public List<Comment> getCommentsByApplication(ApplicationForm application) {
		return commentDAO.getReviewsByApplication(application);
	}

	@Transactional
	public void save(Comment comment) {
		commentDAO.save(comment);
	}

	public Comment getReviewById(int id) {
		return commentDAO.get(id);
	}

	public List<Comment> getVisibleComments(ApplicationForm application, RegisteredUser user) {
		List<Comment> visibleComments = new ArrayList<Comment>();
		List<Comment> allReviewsForApplication = getCommentsByApplication(application);
		for (Comment comment : allReviewsForApplication) {
			if (comment.getUser().isInRole(Authority.REVIEWER) && (!comment.getUser().equals(user))) {
				continue;
			} else {
				visibleComments.add(comment);
			}
		}
		return visibleComments;
	}

}
