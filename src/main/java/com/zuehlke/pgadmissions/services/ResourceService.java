package com.zuehlke.pgadmissions.services;

import java.util.List;

import com.zuehlke.pgadmissions.dto.ResourceConsoleListRowDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ResourceDAO;
import com.zuehlke.pgadmissions.domain.PrismResourceDynamic;

@Service
@Transactional
public class ResourceService {

    @Autowired
    private ResourceDAO resourceDAO;
    
    @Autowired
    private UserService userService;
    
    public <T extends PrismResourceDynamic> List<ResourceConsoleListRowDTO> getConsoleListBlock(Class<T> resourceType, int page, int perPage) {
        return resourceDAO.getConsoleListBlock(userService.getCurrentUser(), resourceType, page, perPage);
    }
    
}
