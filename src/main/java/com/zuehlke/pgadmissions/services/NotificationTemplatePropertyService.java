package com.zuehlke.pgadmissions.services;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.dto.NotificationTemplateModelDTO;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class NotificationTemplatePropertyService {

    private static final Logger log = LoggerFactory.getLogger(NotificationTemplatePropertyService.class);

    @Value("${application.host}")
    private String host;


    public String get(NotificationTemplateModelDTO modelDTO, String[] properties) {
        try {
            Object tempObject = modelDTO;
            for (String property : properties) {
                tempObject = PropertyUtils.getSimpleProperty(tempObject, property);
            }
            return (String) tempObject;
        } catch (Exception e) {
            log.error("Could not traverse through properties chain: " + Arrays.toString(properties));
            throw new Error(e);
        }
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
