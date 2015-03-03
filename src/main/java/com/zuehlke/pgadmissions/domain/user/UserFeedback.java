package com.zuehlke.pgadmissions.domain.user;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleCategory;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

@Entity
@Table(name = "USER_FEEDBACK")
public class UserFeedback {

	@Id
	@GeneratedValue
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "role_category", nullable = false)
	private PrismRoleCategory roleCategory;

	@ManyToOne
	@JoinColumn(name = "institution_id", nullable = false)
	private Institution institution;

    @Column(name = "declined_response", nullable = false)
    private Boolean declinedResponse;

	@Column(name = "rating")
	private Integer rating;

	@Lob
	@Column(name = "content")
	private String content;

    @Lob
    @Column(name = "feature_requests")
    private String featureRequest;

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

    public String getFeatureRequest() {
        return featureRequest;
    }

    public void setFeatureRequest(String featureRequest) {
        this.featureRequest = featureRequest;
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

	public UserFeedback withFeatureRequest(String featureRequest) {
		this.featureRequest = featureRequest;
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
