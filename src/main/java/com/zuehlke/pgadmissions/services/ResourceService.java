package com.zuehlke.pgadmissions.services;

import java.util.List;

import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.rest.dto.InstitutionAddressDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.ProgramDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ResourceDAO;
import com.zuehlke.pgadmissions.dto.ResourceConsoleListRowDTO;

@Service
@Transactional
public class ResourceService {

    @Autowired
    private ResourceDAO resourceDAO;

    @Autowired
    private EntityService entityService;
    
    @Autowired
    private UserService userService;
    
    public <T extends ResourceDynamic> List<ResourceConsoleListRowDTO> getConsoleListBlock(Class<T> resourceType, int page, int perPage) {
        return resourceDAO.getConsoleListBlock(userService.getCurrentUser(), resourceType, page, perPage);
    }

    public <T extends Resource> void reassignState(Class<T> resourceClass, State state, State degradationState) {
        resourceDAO.reassignState(resourceClass, state, degradationState);
    }

    public Resource createNewInstitution(System system, User user, InstitutionDTO institutionDTO) {
        InstitutionDomicile domicile = entityService.getByProperty(InstitutionDomicile.class, "id", institutionDTO.getDomicileId());
        InstitutionAddressDTO addressDTO = institutionDTO.getAddress();
        InstitutionAddress address = new InstitutionAddress().withCountry(domicile).withAddressLine1(addressDTO.getAddressLine1()).withAddressLine2(addressDTO.getAddressLine2()).withAddressTown(addressDTO.getAddressTown()).withAddressCode(addressDTO.getAddressCode());
        return new Institution().withSystem(system).withUser(user).withName(institutionDTO.getName()).withHomepage(institutionDTO.getHomepage())
                .withAddress(address);
    }

    public Resource createNewProgram(Institution institution, User user, ProgramDTO programDTO) {
        return new Program().withInstitution(institution).withUser(user);
    }

    public Resource createNewApplication(Advert advert, User user) {
        return new Application().withProgram(advert.getProgram()).withProject(advert.getProject()).withUser(user);
    }
}
