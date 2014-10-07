package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.dto.NotificationTemplateModelDTO;
import com.zuehlke.pgadmissions.utils.ReflectionUtils;

@Service
@Transactional
public class NotificationTemplatePropertyService {

    @Value("${application.host}")
    private String host;
    
    public String get(NotificationTemplateModelDTO modelDTO, String[] properties) {
        Object tempObject = modelDTO;
        for (String property : properties) {
            tempObject = ReflectionUtils.getProperty(tempObject, property);
        }
        return (String) tempObject;
    }

    public String getProjectOrProgramTitle(NotificationTemplateModelDTO modelDTO) {
        Project project = modelDTO.getResource().getProject();
        Program program = modelDTO.getResource().getProgram();
        return project == null ? program.getTitle() : project.getTitle();
    }

    public String getViewEditControl(NotificationTemplateModelDTO modelDTO) {
        Resource resource = modelDTO.getResource();
        String url;
        if (resource.getResourceScope() == PrismScope.SYSTEM) {
            url = host + "/#/system";
        } else {
            url = host + "/#/" + resource.getResourceScope().getLowerCaseName() + "s/" + resource.getId() + "/view";
        }
        return "<a href=\"" + url + "\">View</a>";
    }

}
