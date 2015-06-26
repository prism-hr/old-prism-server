package com.zuehlke.pgadmissions.domain.advert;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;

@Entity
@Table(name = "ADVERT_PROGRAM", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "imported_program_id" }) })
public class AdvertProgram extends AdvertTarget<ImportedProgram> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @ManyToOne
    @JoinColumn(name = "imported_program_id", nullable = false)
    private ImportedProgram program;

    @Column(name = "importance", nullable = false)
    private BigDecimal importance;

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

    public ImportedProgram getProgram() {
        return program;
    }

    public void setProgram(ImportedProgram program) {
        this.program = program;
    }

    @Override
    public BigDecimal getImportance() {
        return importance;
    }

    @Override
    public void setImportance(BigDecimal importance) {
        this.importance = importance;
    }

    @Override
    public ImportedProgram getValue() {
        return program;
    }
    
    @Override
    public void setValue(ImportedProgram value) {
        setProgram(value);
    }
    
    @Override
    public String getTitle() {
        return program.getName();
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("program", program);
    }

}
