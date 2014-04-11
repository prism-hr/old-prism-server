package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.enums.AdvertState;
import com.zuehlke.pgadmissions.domain.enums.AdvertType;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity
@Table(name = "ADVERT")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Advert implements Serializable {
    private static final long serialVersionUID = 5963260213501162814L;

    @Id
    @GeneratedValue
    private Integer id;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 255)
    @Column(name = "title")
    private String title;

    @Size(max = 3000, message = "A maximum of 2000 characters are allowed.")
    @Column(name = "description", nullable = false)
    private String description = "Advert coming soon!";

    @Column(name = "study_duration")
    private Integer studyDuration;

    @Size(max = 2000, message = "A maximum of 1000 characters are allowed.")
    @Column(name = "funding")
    private String funding;

    @Column(name = "state_id")
    @Enumerated(EnumType.STRING)
    private AdvertState state;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User contactUser;

    @Column(name = "advert_type")
    @Enumerated(EnumType.STRING)
    private AdvertType advertType;

    @OneToOne
    @JoinColumn(name = "advert_closing_date_id")
    private AdvertClosingDate closingDate;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "program_id", nullable = false)
    private List<AdvertClosingDate> closingDates = new ArrayList<AdvertClosingDate>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getFunding() {
        return funding;
    }

    public void setFunding(String funding) {
        this.funding = funding;
    }

    public String getDescriptionForFacebook() {
        return getStudyDurationToRead().toLowerCase().replace("s", "")
                + " research study programme delivered by UCL Engineering at London's global University. "
                + "Click to find out more about the programme and apply for your place.";
    }

    public String getStudyDurationToRead() {
        Integer studyDurationToRead = studyDuration;
        String timeIntervalToRead = "Month";

        if (studyDuration % 12 == 0) {
            studyDurationToRead = studyDuration / 12;
            timeIntervalToRead = "Year";
        }

        if (studyDurationToRead > 1) {
            timeIntervalToRead = timeIntervalToRead + "s";
        }

        return studyDurationToRead.toString() + " " + timeIntervalToRead;
    }

    public User getContactUser() {
        return contactUser;
    }

    public void setContactUser(User contactUser) {
        this.contactUser = contactUser;
    }

    public AdvertType getAdvertType() {
        return advertType;
    }

    public void setAdvertType(AdvertType advertType) {
        this.advertType = advertType;
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

    public AdvertState getState() {
        return state;
    }

    public void setState(AdvertState state) {
        this.state = state;
    }

    public boolean isEnabled() {
        return state == AdvertState.PROGRAM_APPROVED || state == AdvertState.PROJECT_APPROVED;
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

    public abstract Program getProgram();

    public abstract Project getProject();

}
