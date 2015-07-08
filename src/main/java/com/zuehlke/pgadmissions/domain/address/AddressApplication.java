package com.zuehlke.pgadmissions.domain.address;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import uk.co.alumeni.prism.api.model.resource.AddressDefinition;

import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;

@Entity
@Table(name = "address")
public class AddressApplication extends Address implements AddressDefinition<ImportedEntitySimple> {

    @ManyToOne
    @JoinColumn(name = "imported_domicile_id")
    private ImportedEntitySimple domicile;

    @NotEmpty
    @Column(name = "address_line_1", nullable = false)
    private String addressLine1;

    @Column(name = "address_line_2")
    private String addressLine2;

    @NotEmpty
    @Column(name = "address_town", nullable = false)
    private String addressTown;

    @Column(name = "address_region")
    private String addressRegion;

    @Column(name = "address_code")
    private String addressCode;

    @Override
    public ImportedEntitySimple getDomicile() {
        return domicile;
    }

    @Override
    public void setDomicile(ImportedEntitySimple domicile) {
        this.domicile = domicile;
    }

    @Override
    public String getLocationString() {
        return super.getLocationString() + ", " + domicile.getName();
    }

}
