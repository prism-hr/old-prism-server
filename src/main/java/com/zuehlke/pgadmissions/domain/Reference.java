package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

@Entity(name = "REFERENCE")
@Access(AccessType.FIELD)
public class Reference extends DomainObject<Integer> {


	private static final long serialVersionUID = 5269362387094590530L;
	
	@OneToOne(orphanRemoval=true, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "document_id")
	private Document document;
	
	@OneToOne(orphanRemoval=true, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "comment_id")
	private Comment comment;
	
	@Column(name="suitable_for_UCL")
	private Boolean suitableForUCL;
	
	@Column(name="suitable_for_Programme")
	private Boolean suitableForProgramme;
	
	@OneToOne(mappedBy ="reference")
	private Referee referee;
	
	
	@Column(name = "updated_time_stamp", insertable = false)
	@Generated(GenerationTime.ALWAYS)
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdated;
	
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

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public Referee getReferee() {
		return referee;
	}

	public void setReferee(Referee referee) {
		this.referee = referee;
	}


	public Date getLastUpdated() {
		return lastUpdated;
	}

	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

	public Boolean getSuitableForUCL() {
		return suitableForUCL;
	}

	public void setSuitableForUCL(Boolean suitableForUCL) {
		this.suitableForUCL = suitableForUCL;
	}

	public Boolean getSuitableForProgramme() {
		return suitableForProgramme;
	}

	public void setSuitableForProgramme(Boolean suitableForProgramme) {
		this.suitableForProgramme = suitableForProgramme;
	}

	
}
