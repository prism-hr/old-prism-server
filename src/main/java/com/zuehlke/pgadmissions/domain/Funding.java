package com.zuehlke.pgadmissions.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.enums.FundingType;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity
@Table(name = "APPLICATION_FUNDING")
public class Funding {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "award_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private FundingType type;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 2000)
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "award_value", nullable = false)
    @ESAPIConstraint(rule = "NumbersOnly", maxLength = 100)
    private String value;

    @Column(name = "award_date", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate awardDate;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false, insertable = false, updatable = false)
    private Application application;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public FundingType getType() {
        return type;
    }

    public void setType(FundingType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getValueAsInteger() {
        if (value != null) {
            return Integer.parseInt(getValue());
        }
        return 0;
    }

    public LocalDate getAwardDate() {
        return awardDate;
    }

    public void setAwardDate(LocalDate awardDate) {
        this.awardDate = awardDate;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Funding withId(Integer id) {
        this.id = id;
        return this;
    }

    public Funding withType(FundingType type) {
        this.type = type;
        return this;
    }

    public Funding withDocument(Document document) {
        this.document = document;
        return this;
    }

    public Funding withDescription(String description) {
        this.description = description;
        return this;
    }

    public Funding withValue(String value) {
        this.value = value;
        return this;
    }

    public Funding withAwardDate(LocalDate awardDate) {
        this.awardDate = awardDate;
        return this;
    }

    public Funding withApplication(Application application) {
        this.application = application;
        return this;
    }
}
