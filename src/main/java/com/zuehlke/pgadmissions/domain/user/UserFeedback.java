package com.zuehlke.pgadmissions.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleCategory;
import com.zuehlke.pgadmissions.domain.institution.Institution;

@Entity
@Table(name = "USER_FEEDBACK")
public class UserFeedback {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "user", nullable = false)
	private User user;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "role_category", nullable = false)
	private PrismRoleCategory roleCategory;
	
	@ManyToOne
	@JoinColumn(name = "institution", nullable = false)
	private Institution institution;
	
    @Column(name = "declined_response", nullable = false)
    private Boolean declinedResponse;
	
	@Column(name = "rating")
	private Integer rating;
	
	@Column(name = "content")
	private String content;
	
	@Column(name = "recommended")
	private Boolean recommended;
	
	@Column(name = "created_timestamp", nullable = false)
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime createdTimestamp;
	
	@Column(name = "sequence_identifier")
	private String sequenceIdentifier;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public PrismRoleCategory getRoleCategory() {
		return roleCategory;
	}

	public void setRoleCategory(PrismRoleCategory roleCategory) {
		this.roleCategory = roleCategory;
	}

	public Institution getInstitution() {
		return institution;
	}

	public void setInstitution(Institution institution) {
		this.institution = institution;
	}
	
	public Boolean getDeclinedResponse() {
		return declinedResponse;
	}

	public void setDeclinedResponse(Boolean declinedResponse) {
		this.declinedResponse = declinedResponse;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Boolean getRecommended() {
		return recommended;
	}

	public void setRecommended(Boolean recommended) {
		this.recommended = recommended;
	}

	public DateTime getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(DateTime createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public String getSequenceIdentifier() {
		return sequenceIdentifier;
	}

	public void setSequenceIdentifier(String sequenceIdentifier) {
		this.sequenceIdentifier = sequenceIdentifier;
	}

	public UserFeedback withUser(User user) {
		this.user = user;
		return this;
	}

	public UserFeedback withRoleCategory(PrismRoleCategory roleCategory) {
		this.roleCategory = roleCategory;
		return this;
	}

	public UserFeedback withInstitution(Institution institution) {
		this.institution = institution;
		return this;
	}
	
	public UserFeedback withDeclinedResponse(Boolean declinedResponse) {
		this.declinedResponse = declinedResponse;
		return this;
	}
	
	public UserFeedback withRating(Integer rating) {
		this.rating = rating;
		return this;
	}
	
	public UserFeedback withContent(String content) {
		this.content = content;
		return this;
	}

	public UserFeedback withRecommended(Boolean recommended) {
		this.recommended = recommended;
		return this;
	}
	
	public UserFeedback withCreatedTimestamp(DateTime createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
		return this;
	}
	
}
