package com.zuehlke.pgadmissions.domain;

import java.util.ArrayList;

import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

@Entity(name = "REVIEW_ROUND")
@Access(AccessType.FIELD)
public class ReviewRound extends DomainObject<Integer> {


	private static final long serialVersionUID = 1068777060574638531L;
	
	@OneToMany(cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "review_round_id")
	private List<Reviewer> reviewers = new ArrayList<Reviewer>();	
	
	@ManyToOne
	@JoinColumn(name = "application_form_id")	
	private ApplicationForm application;	
	
	
	@Column(name = "created_date", insertable = false)
	@Generated(GenerationTime.INSERT)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	
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

	public List<Reviewer> getReviewers() {
		return reviewers;
	}

	public void setReviewers(List<Reviewer> reviewers) {
		this.reviewers = reviewers;
	}

	public ApplicationForm getApplication() {
		return application;
	}

	public void setApplication(ApplicationForm application) {
		this.application = application;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}


}
