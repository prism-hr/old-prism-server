package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.IndexColumn;

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.CommentPropertyType;

@Entity(name = "COMMENT")
@Inheritance(strategy = InheritanceType.JOINED)
public class Comment implements Serializable {

    private static final long serialVersionUID = 2861325991249900547L;

    @Id
    @GeneratedValue
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_form_id")
    private ApplicationForm application = null;

    @Column(name = "content")
    @Size(max = 50000, message = "A maximum of 50000 characters are allowed.")
    @Lob
    private String comment = null;
    
    @Column(name = "declined")
	private boolean declined = false;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "comment_property_type_id")
    private ApplicationFormAction action = ApplicationFormAction.COMMENT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private RegisteredUser user = null;
    
    @Column(name = "created_timestamp", insertable = false)
    @Generated(GenerationTime.INSERT)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private List<Document> documents = new ArrayList<Document>();
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private List<CommentProperty> commentProperties = new ArrayList<CommentProperty>();
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id")
    @IndexColumn(name = "score_position")
    private List<Score> scores = new ArrayList<Score>();

    @Transient
    private Boolean confirmNextStage;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ApplicationForm getApplication() {
		return application;
	}

	public void setApplication(ApplicationForm application) {
		this.application = application;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		if (comment == null) {
			this.comment = getAction().toString();
		}
		this.comment = comment;
	}

	public boolean isDeclined() {
		return declined;
	}

	public void setDeclined(boolean declined) {
		this.declined = declined;
	}

	public ApplicationFormAction getAction() {
		return action;
	}

	public void setType(ApplicationFormAction action) {
		this.action = action;
	}

	public RegisteredUser getUser() {
		return user;
	}

	public void setUser(RegisteredUser user) {
		this.user = user;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}
	
	public void addDocuments(List<Document> documents) {
		for (Document document : documents) {
			if (!this.documents.contains(document)) {
				this.documents.add(document);
			}
		}
	}

	public List<CommentProperty> getCommentProperties() {
		return commentProperties;
	}
	
	public void addCommentProperties(List<CommentProperty> commentProperties) {
		for (CommentProperty property : commentProperties) {
			if (!this.commentProperties.contains(property)) {
				this.commentProperties.add(property);
			}
		}
	}

	public void setCommentProperties(List<CommentProperty> commentProperties) {
		this.commentProperties = commentProperties;
	}
	
	protected CommentProperty getCommentProperty(CommentPropertyType propertyType) {
		for (CommentProperty property : getCommentProperties()) {
			if (property.getCommentPropertyType() == propertyType) {
				return property;
			}
		}
		return null;
	}
	
	protected void setCommentProperty(CommentPropertyType propertyType, Object... propertyValues) {
		CommentProperty property = new CommentProperty();
		property.setCommentPropertyType(propertyType);
		for (Object propertyValue : propertyValues) {
			if (propertyValue instanceof String) {
				if (((String) propertyValue).length() <= 50) {
					property.setValueVarchar((String) propertyValue);
				}
				property.setValueText((String) propertyValue);
			} else if (propertyValue instanceof Integer) {
				property.setValueInteger((Integer) propertyValue);
			} else if (propertyValue instanceof Boolean) {
				property.setValueBoolean((Boolean) propertyValue);
			} else if (propertyValue instanceof BigDecimal) {
				property.setValueDecimal((BigDecimal) propertyValue);
			} else {
				property.setValueDatetime((Date) propertyValue);
			}
		}
		this.commentProperties.add(property);
	}

	public List<Score> getScores() {
		return scores;
	}

	public void setScores(List<Score> scores) {
		this.scores = scores;
	}

	public Boolean getConfirmNextStage() {
		return confirmNextStage;
	}

	public void setConfirmNextStage(Boolean confirmNextStage) {
		this.confirmNextStage = confirmNextStage;
	}

	public String getTooltipMessage(final String role) {
        return String.format("%s %s (%s) as: %s", user.getFirstName(), user.getLastName(), user.getEmail(), StringUtils.capitalize(role));
    }
    
}