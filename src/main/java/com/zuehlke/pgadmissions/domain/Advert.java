package com.zuehlke.pgadmissions.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.google.common.base.Objects;

@Entity
@Table(name = "ADVERT")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Advert extends PrismResourceDynamic {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "description")
    private String description;

    @Column(name = "month_study_duration")
    private Integer studyDuration;
    
    @ManyToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advert_closing_date_id", unique = true)
    private AdvertClosingDate closingDate;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "advert_id", nullable = false)
    private List<AdvertClosingDate> closingDates = new ArrayList<AdvertClosingDate>();

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStudyDuration() {
        return studyDuration;
    }

    public void setStudyDuration(Integer studyDuration) {
        this.studyDuration = studyDuration;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
