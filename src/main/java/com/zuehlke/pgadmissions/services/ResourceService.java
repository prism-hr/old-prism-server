package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ResourceDAO;
import com.zuehlke.pgadmissions.domain.PrismResourceDynamic;
import com.zuehlke.pgadmissions.domain.User;

@Service
@Transactional
public class ResourceService {

    @Autowired
    private ResourceDAO resourceDAO;
    
    public List<PrismResourceDynamic> getConsoleList(Class<? extends PrismResourceDynamic> clazz, User user, int pageIndex, int rowsPerPage) {
        return resourceDAO.getConsoleList(clazz, user, pageIndex, rowsPerPage);
    }
    
}
