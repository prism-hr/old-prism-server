package com.zuehlke.pgadmissions.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.zuehlke.pgadmissions.domain.enums.AdvertType;

@Entity
@Table(name = "PROJECT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Project extends Advert implements PrismScope {

    private static final long serialVersionUID = 5963260213501162814L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    public Project() {
        super.setAdvertType(AdvertType.PROJECT);
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    @Override
    public Program getProgram() {
        return program;
    }

    @Override
    public Project getProject() {
        return this;
    }

}
