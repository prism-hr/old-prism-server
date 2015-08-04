package com.zuehlke.pgadmissions.domain.address;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import uk.co.alumeni.prism.api.model.resource.AddressDefinition;

import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;

@Entity
@Table(name = "address")
public class AddressApplication extends Address implements AddressDefinition<ImportedEntitySimple> {

    @ManyToOne
    @JoinColumn(name = "imported_domicile_id")
    private ImportedEntitySimple domicile;

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
