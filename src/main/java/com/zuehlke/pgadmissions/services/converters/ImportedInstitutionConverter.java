package com.zuehlke.pgadmissions.services.converters;

import org.apache.commons.beanutils.PropertyUtils;

import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.services.EntityService;

public class ImportedInstitutionConverter extends ImportedEntityConverter<ImportedInstitution> {

    private final EntityService entityService;

    public ImportedInstitutionConverter(Institution institution, EntityService entityService) {
        super(institution, ImportedInstitution.class);
        this.entityService = entityService;
    }

    protected void setCustomProperties(Object input, ImportedInstitution result) throws Exception {
        String domicileCode = (String) PropertyUtils.getSimpleProperty(input, "domicile");
        Domicile domicile = entityService.getByProperties(Domicile.class, ImmutableMap.of("code", (Object) domicileCode, "enabled", true));
        result.setDomicile(domicile);
    }

}
