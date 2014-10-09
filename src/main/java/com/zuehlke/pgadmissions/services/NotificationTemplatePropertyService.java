package com.zuehlke.pgadmissions.services;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
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
    
    @Autowired
    private CommentService commentService;

    public String get(NotificationTemplateModelDTO modelDTO, String[] properties) {
        Object tempObject = modelDTO;
        int i = 0;
        for (String property : properties) {
            if(tempObject == null){
                String[] subArray = Arrays.copyOf(properties, i);
                throw new NullPointerException("Value of given property (of NotificationTemplateModelDTO) is null: " + Joiner.on(".").join(subArray));
            }
            tempObject = ReflectionUtils.getProperty(tempObject, property);
            i++;
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
    
    public String getRejectionReason(NotificationTemplateModelDTO modelDTO) {
        Application application = (Application) modelDTO.getResource();
        Comment rejection = commentService.getRejectionComment(application);
        return rejection == null ? null : rejection.getRejectionReasonDisplay();
    }
    
}
