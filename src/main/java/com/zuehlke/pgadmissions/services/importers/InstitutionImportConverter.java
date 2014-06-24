package com.zuehlke.pgadmissions.services.importers;

import com.zuehlke.pgadmissions.dao.EntityDAO;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.LanguageQualificationType;
import com.zuehlke.pgadmissions.services.EntityService;
import org.apache.commons.beanutils.PropertyUtils;

public class InstitutionImportConverter extends GenericEntityImportConverter<ImportedInstitution> {

    private final EntityService entityService;

    public InstitutionImportConverter(Institution institution, EntityService entityService) {
        super(ImportedInstitution.class, institution);
        this.entityService = entityService;
    }

    protected void setCustomProperties(Object input, ImportedInstitution result) throws Exception {
        String domicileCode = (String) PropertyUtils.getSimpleProperty(input, "domicile");
        Domicile domicile = entityService.getByCode(Domicile.class, domicileCode);
        result.setDomicile(domicile);
    }


}