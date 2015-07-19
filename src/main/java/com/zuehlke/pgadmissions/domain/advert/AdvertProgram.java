package com.zuehlke.pgadmissions.domain.advert;

import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;

import javax.persistence.*;

@Entity
@Table(name = "advert_program", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "imported_program_id" }) })
public class AdvertProgram extends AdvertTarget<ImportedProgram> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @ManyToOne
    @JoinColumn(name = "imported_program_id", nullable = false)
    private ImportedProgram value;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = false;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Advert getAdvert() {
        return advert;
    }

    @Override
    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    @Override
    public ImportedProgram getValue() {
        return value;
    }

    @Override
    public void setValue(ImportedProgram program) {
        this.value = program;
    }

    @Override
    public Integer getValueId() {
        return value.getId();
    }

    @Override
    public String getTitle() {
        return value.getName();
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
