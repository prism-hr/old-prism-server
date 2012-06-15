package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import com.zuehlke.pgadmissions.domain.enums.CommentType;


@Entity(name="COMMENT")
@Inheritance(strategy = InheritanceType.JOINED)
@Access(AccessType.FIELD) 
public class Comment extends DomainObject<Integer> implements Comparable<Comment> {

	private static final long serialVersionUID = 2861325991249900547L;

	@Column(name = "created_timestamp", insertable = false)
	@Generated(GenerationTime.INSERT)
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;
	
	private String comment;
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public RegisteredUser getUser() {
		return user;
	}

	public void setUser(RegisteredUser user) {
		this.user = user;
	}

	public ApplicationForm getApplication() {
		return application;
	}

	public void setApplication(ApplicationForm application) {
		this.application = application;
	}

	@ManyToOne
	@JoinColumn(name="user_id")
	private RegisteredUser user = null;
	
	@ManyToOne
	@JoinColumn(name="application_form_id")
	private ApplicationForm application = null;
	
	
	@Override
	public void setId(Integer id) {
		this.id = id;		
	}

	@Override
	@Id
	@GeneratedValue
	@Access(AccessType.PROPERTY)
	public Integer getId() {
		return id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date createdTimestamp) {
		this.date = createdTimestamp;
	}

	public CommentType getType() {
		return CommentType.GENERIC;
	}

	@Override
	public int compareTo(Comment otherComment) {
		return otherComment.getDate().compareTo(this.date);
	}	

}
