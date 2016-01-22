package com.zuehlke.pgadmissions.workflow.notification.property;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NOTIFICATION_TEMPLATE_CONSIDER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_OR;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.department.Department;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.dto.NotificationDefinitionModelDTO;
import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Component
public class ApplicationRejectionRecommendBuilder implements NotificationPropertyBuilder {

	@Value("${application.api.url}")
	private String applicationApiUrl;

	@Override
	public String build(NotificationPropertyLoader propertyLoader) throws Exception {
		PropertyLoader loader = propertyLoader.getPropertyLoader();
		NotificationDefinitionModelDTO modelDTO = propertyLoader.getNotificationDefinitionModelDTO();

		String field = "";
		if (BooleanUtils.isTrue(modelDTO.getComment().getRejectionRecommend())) {
			Resource resource = modelDTO.getResource();
			Integer userId = modelDTO.getUser().getId();
			Integer applicationId = resource.getId();

			Program program = resource.getProgram();
			Department department = program == null ? null : program.getDepartment();

			field = field + loader.load(SYSTEM_NOTIFICATION_TEMPLATE_CONSIDER) + " ";
			if (department != null) {
				field = field + getRedirectLink(userId, applicationId, "department", department.getId(),
						department.getTitle()) + " " + loader.load(SYSTEM_OR) + " ";
			}

			Institution institution = resource.getInstitution();
			field = field
					+ getRedirectLink(userId, applicationId, "institution", institution.getId(), institution.getTitle())
					+ ". ";
		}
		return field + loader.load(PrismDisplayPropertyDefinition.SYSTEM_NOTIFICATION_TEMPLATE_WISH_SUCCESS) + ".";
	}

	private String getRedirectLink(Integer userId, Integer applicationId, String nodeType, Integer nodeId,
			String nodeTitle) {
		String url = applicationApiUrl + "/mail/public";
		return "<a href='" + url + "?rejectedApplicant=" + userId + "&rejectedApplication=" + applicationId + "&"
				+ nodeType + "=" + nodeId + "'>" + nodeTitle + "</a>";
	}

}
