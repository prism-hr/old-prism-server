package com.zuehlke.pgadmissions.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "ADVERT_RECRUITMENT_PREFERENCE", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "institution_id" }),
        @UniqueConstraint(columnNames = { "advert_id", "program_id" }) })
public class AdvertRecruitmentPreference {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private Program program;

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public final Advert getAdvert() {
        return advert;
    }

    public final void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public final Institution getInstitution() {
        return institution;
    }

    public final void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public final Program getProgram() {
        return program;
    }

    public final void setProgram(Program program) {
        this.program = program;
    }

}
