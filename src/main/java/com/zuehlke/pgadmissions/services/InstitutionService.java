package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.InstitutionDAO;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.InstitutionAddress;
import com.zuehlke.pgadmissions.domain.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.InstitutionDomicileRegion;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.rest.dto.InstitutionAddressDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;

@Service
@Transactional
public class InstitutionService {

    @Autowired
    private EntityService entityService;

    @Autowired
    private InstitutionDAO institutionDAO;

    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private StateService stateService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private ApplicationContext applicationContext;

    public Institution getByCode(String institutionCode) {
        return institutionDAO.getByCode(institutionCode);
    }

    public List<ImportedInstitution> getEnabledImportedInstitutionsByDomicile(Domicile domicile) {
        return institutionDAO.getEnabledImportedInstitutionsByDomicile(domicile);
    }

    public List<InstitutionDomicileRegion> getTopLevelRegions(InstitutionDomicile domicile) {
        return institutionDAO.getTopLevelRegions(domicile);
    }

    public List<Institution> listByCountry(InstitutionDomicile domicile) {
        return institutionDAO.listByCountry(domicile);
    }

    public Institution getUclInstitution() {
        return institutionDAO.getUclInstitution();
    }

    public Institution create(User user, InstitutionDTO institutionDTO) {
        InstitutionAddressDTO institutionAddressDTO = institutionDTO.getAddress();
        InstitutionDomicile institutionAddressCountry = entityService.getById(InstitutionDomicile.class, institutionAddressDTO.getCountry());
        InstitutionDomicileRegion institutionAddressRegion = entityService.getById(InstitutionDomicileRegion.class, institutionAddressDTO.getRegion());
        
        InstitutionAddress institutionAddress = new InstitutionAddress().withAddressLine1(institutionAddressDTO.getAddressLine1())
                .withAddressLine2(institutionAddressDTO.getAddressLine2()).withAddressTown(institutionAddressDTO.getAddressTown())
                .withAddressDistrict(institutionAddressDTO.getAddressDistrict()).withAddressCode(institutionAddressDTO.getAddressCode())
                .withRegion(institutionAddressRegion).withCountry(institutionAddressCountry);
        
        InstitutionDomicile institutionCountry = entityService.getById(InstitutionDomicile.class, institutionDTO.getDomicile());
        
        return new Institution().withSystem(systemService.getSystem()).withDomicile(institutionCountry).withAddress(institutionAddress)
                .withName(institutionDTO.getName()).withHomepage(institutionDTO.getHomepage()).withUser(user);
    }

    public void save(Institution institution) {
        InstitutionAddress institutionAddress = institution.getAddress();
        entityService.save(institutionAddress, institution);
    }
    
    public void populateDefaultImportedEntityFeeds() {
        for (Institution institution : institutionDAO.getInstitutionsWithoutImportedEntityFeeds()) {
            for (PrismImportedEntity importedEntityType : PrismImportedEntity.values()) {
                if (importedEntityType.getDefaultLocation() != null) {
                    importedEntityService.getOrCreateImportedEntityFeed(institution, importedEntityType, importedEntityType.getDefaultLocation());
                }
            }
        }
    }

}
