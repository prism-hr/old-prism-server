package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import com.zuehlke.pgadmissions.domain.enums.CommentPropertyType;

@Entity(name = "COMMENT_PROPERTY")
public class CommentProperty implements Serializable {

	private static final long serialVersionUID = 7492525863686665555L;

	@Id
    @GeneratedValue
    private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "comment_id")
	private Comment comment;

    @Column(name = "comment_property_type_id")
    @Enumerated(EnumType.STRING)
    private CommentPropertyType commentPropertyType;

    @Column(name = "value_varchar")
    private String valueVarchar = null;

    @Column(name = "value_integer")
    private Integer valueInteger = null;
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "value_integer")
    private Reviewer reviewer = null;
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "value_integer")
    private Reviewer interviewer = null;
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "value_integer")
    private Reviewer supervisor = null;
    
    @Column(name = "value_boolean")
    private Boolean valueBoolean = null;
    
    @Column(name = "value_decimal", precision = 3, scale = 2)
    private BigDecimal valueDecimal = null;
    
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "value_datetime")
    private Date valueDatetime = null;
    
    @Column(name = "value_text")
    @Size(max = 50000, message = "A maximum of 50000 characters are allowed.")
    @Lob
    private String valueText = null;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

	public CommentPropertyType getCommentPropertyType() {
		return commentPropertyType;
	}

	public void setCommentPropertyType(CommentPropertyType commentPropertyType) {
		this.commentPropertyType = commentPropertyType;
	}

	public String getValueVarchar() {
		return valueVarchar;
	}

	public void setValueVarchar(String valueVarchar) {
		this.valueVarchar = valueVarchar;
	}

	public Integer getValueInteger() {
		return valueInteger;
	}

	public void setValueInteger(Integer valueInteger) {
		this.valueInteger = valueInteger;
	}

	public Reviewer getReviewer() {
		return reviewer;
	}

	public void setReviewer(Reviewer reviewer) {
		this.reviewer = reviewer;
	}

	public Reviewer getInterviewer() {
		return interviewer;
	}

	public void setInterviewer(Reviewer interviewer) {
		this.interviewer = interviewer;
	}

	public Reviewer getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(Reviewer supervisor) {
		this.supervisor = supervisor;
	}

	public Boolean getValueBoolean() {
		return valueBoolean;
	}

	public void setValueBoolean(Boolean valueBoolean) {
		this.valueBoolean = valueBoolean;
	}

	public BigDecimal getValueDecimal() {
		return valueDecimal;
	}

	public void setValueDecimal(BigDecimal valueDecimal) {
		this.valueDecimal = valueDecimal;
	}

	public Date getValueDatetime() {
		return valueDatetime;
	}

	public void setValueDatetime(Date valueDatetime) {
		this.valueDatetime = valueDatetime;
	}

	public String getValueText() {
		return valueText;
	}

	public void setValueText(String valueText) {
		this.valueText = valueText;
	}
    
}