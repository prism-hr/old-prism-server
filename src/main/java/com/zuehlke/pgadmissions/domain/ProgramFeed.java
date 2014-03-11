package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name = "PROGRAM_FEED")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ProgramFeed extends Authorisable implements Serializable {

    private static final long serialVersionUID = -9073611033741317582L;

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "feed_url")
    private String feedUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private QualificationInstitution institution;

    @OneToMany(mappedBy = "programFeed")
    private List<Program> programs = new ArrayList<Program>();

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }

    public QualificationInstitution getInstitution() {
        return institution;
    }

    public void setInstitution(QualificationInstitution institution) {
        this.institution = institution;
    }

    public List<Program> getPrograms() {
        return programs;
    }

}
