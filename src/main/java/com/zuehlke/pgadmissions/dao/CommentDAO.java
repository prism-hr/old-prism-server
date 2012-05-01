package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Comment;

@Repository
public class CommentDAO {

	private final SessionFactory sessionFactory;
	
	CommentDAO(){
		this(null);
	}
	
	@Autowired
	public CommentDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void save(Comment comment) {
		sessionFactory.getCurrentSession().saveOrUpdate(comment);
	}
	
	public Comment get(Integer id) {
		return (Comment) sessionFactory.getCurrentSession().get(
				Comment.class, id);
	}
	
	
	
}
