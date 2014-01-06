package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import com.zuehlke.pgadmissions.utils.MathUtils;

@Entity(name = "REVIEW_ROUND")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ReviewRound implements Serializable {

    private static final long serialVersionUID = 1068777060574638531L;

    @Id
    @GeneratedValue
    private Integer id;

    @OneToMany(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
    @org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    @JoinColumn(name = "review_round_id")
    private List<Reviewer> reviewers = new ArrayList<Reviewer>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_form_id")
    private ApplicationForm application;

    @Column(name = "created_date", insertable = false)
    @Generated(GenerationTime.INSERT)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    
    @Column(name = "completed_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date completedDate;

    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating;
    
    @Column(name = "total_negative_endorsements")
    private Integer totalPositiveEndorsements = 0;
    
    @Column(name = "total_negative_endorsements")
    private Integer totalNegativeEndorsements = 0;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public List<Reviewer> getReviewers() {
        return reviewers;
    }

    public void setReviewers(List<Reviewer> reviewers) {
        this.reviewers.clear();
        for (Reviewer reviewer : reviewers) {
            if (reviewer != null) {
                this.reviewers.add(reviewer);
            }
        }
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

    public Date getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
	}

	public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getTotalPositiveEndorsements() {
		return totalPositiveEndorsements;
	}

	public void setTotalPositiveEndorsements(Integer totalPositiveEndorsements) {
		this.totalPositiveEndorsements = totalPositiveEndorsements;
	}

	public Integer getTotalNegativeEndorsements() {
		return totalNegativeEndorsements;
	}

	public void setTotalNegativeEndorsements(Integer totalNegativeEndorsements) {
		this.totalNegativeEndorsements = totalNegativeEndorsements;
	}

	public boolean hasAllReviewersResponded() {
        for (Reviewer reviewer : getReviewers()) {
            if (reviewer.getReview() == null) {
                return false;
            }
        }
        return true;
    }
    
    public Integer getAverageRatingPercent(){
        return MathUtils.convertRatingToPercent(getAverageRating());
    }
    
    public String getAverageRatingFormatted(){
        return MathUtils.formatRating(getAverageRating());
    }

}