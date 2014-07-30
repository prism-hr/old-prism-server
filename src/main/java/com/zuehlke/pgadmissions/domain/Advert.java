package com.zuehlke.pgadmissions.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.DurationUnit;

@Entity
@Table(name = "ADVERT")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Advert extends Resource {
    
    @Id
    @GeneratedValue
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "description")
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "institution_address_id")
    private InstitutionAddress address;

    @Column(name = "month_study_duration")
    private Integer studyDuration;
    
    @Column(name = "fee_interval")
    @Enumerated(EnumType.STRING)
    private DurationUnit feeInterval;
    
    @Column(name = "fee_value")
    private BigDecimal feeValue;
    
    @Column(name = "fee_annualised")
    private BigDecimal feeAnnualised;
    
    @Column(name = "pay_interval")
    @Enumerated(EnumType.STRING)
    private DurationUnit payInterval;
    
    @Column(name = "pay_value")
    private BigDecimal payValue;
    
    @Column(name = "pay_annualised")
    private BigDecimal payAnnualised;
    
    @OneToOne
    @JoinColumn(name = "advert_closing_date_id", unique = true)
    private AdvertClosingDate closingDate;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "advert_id", nullable = false)
    private List<AdvertClosingDate> closingDates = new ArrayList<AdvertClosingDate>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "OPPORTUNITY_CATEGORY", joinColumns = @JoinColumn(name = "advert_id"), inverseJoinColumns = @JoinColumn(name = "advert_opportunity_category_id"))
    private Set<OpportunityCategory> categories = Sets.newHashSet();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "ADVERT_PREFERRED_RECRUITER", joinColumns = { @JoinColumn(name = "advert_id", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "institution_id", nullable = false) })
    private Set<Institution> preferredRecruiters = Sets.newHashSet();
    
    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public InstitutionAddress getAddress() {
        return address;
    }

    public void setAddress(InstitutionAddress address) {
        this.address = address;
    }

    public Integer getStudyDuration() {
        return studyDuration;
    }

    public DurationUnit getFeeInterval() {
        return feeInterval;
    }

    public void setFeeInterval(DurationUnit feeInterval) {
        this.feeInterval = feeInterval;
    }

    public BigDecimal getFeeValue() {
        return feeValue;
    }

    public void setFeeValue(BigDecimal feeValue) {
        this.feeValue = feeValue;
    }

    public BigDecimal getFeeAnnualised() {
        return feeAnnualised;
    }

    public void setFeeAnnualised(BigDecimal feeAnnualised) {
        this.feeAnnualised = feeAnnualised;
    }

    public DurationUnit getPayInterval() {
        return payInterval;
    }

    public void setPayInterval(DurationUnit payInterval) {
        this.payInterval = payInterval;
    }

    public BigDecimal getPayValue() {
        return payValue;
    }

    public void setPayValue(BigDecimal payValue) {
        this.payValue = payValue;
    }

    public BigDecimal getPayAnnualised() {
        return payAnnualised;
    }

    public void setPayAnnualised(BigDecimal payAnnualised) {
        this.payAnnualised = payAnnualised;
    }

    public void setStudyDuration(Integer studyDuration) {
        this.studyDuration = studyDuration;
    }

    public AdvertClosingDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(AdvertClosingDate closingDate) {
        this.closingDate = closingDate;
    }

    public List<AdvertClosingDate> getClosingDates() {
        return closingDates;
    }
    
    public Set<OpportunityCategory> getCategories() {
        return categories;
    }

    public Set<Institution> getPreferredRecruiters() {
        return preferredRecruiters;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Advert other = (Advert) obj;
        return Objects.equal(id, other.getId());
    }
    
    public abstract String getTitle();
    
    public abstract void setTitle(String title);

}
