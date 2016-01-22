package com.zuehlke.pgadmissions.workflow.notification.property;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NOTIFICATION_TEMPLATE_CONSIDER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_OR;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.department.Department;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.NotificationDefinitionModelDTO;
import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Component
public class ApplicationRejectionRecommendBuilder implements NotificationPropertyBuilder {

	@Value("${application.api.url}")
	private String applicationApiUrl;

	@Override
	public String build(NotificationPropertyLoader propertyLoader) throws Exception {
		NotificationDefinitionModelDTO modelDTO = propertyLoader.getNotificationDefinitionModelDTO();
		if (BooleanUtils.isTrue(modelDTO.getComment().getRejectionRecommend())) {
			User user = modelDTO.getUser();
			Resource resource = modelDTO.getResource();

			Program program = resource.getProgram();
			Department department = program == null ? null : program.getDepartment();

			PropertyLoader loader = propertyLoader.getPropertyLoader();
			String field = loader.load(SYSTEM_NOTIFICATION_TEMPLATE_CONSIDER) + " ";
			if (department != null) {
				field = field + getRedirectLink(user, "department", department.getId(), department.getTitle()) + " "
						+ loader.load(SYSTEM_OR) + " ";
			}

			Institution institution = resource.getInstitution();
			return field + getRedirectLink(user, "institution", institution.getId(), institution.getTitle()) + ".";
		}
		return null;
	}

	private String getRedirectLink(User user, String nodeType, Integer nodeId, String nodeTitle) {
		String url = applicationApiUrl + "/mail/activate";
		return "<a href='" + url + "?rejectedApplicant=" + user.getId() + "&" + nodeType + "s=" + nodeId
				+ "&activationCode=" + user.getActivationCode() + "'>" + nodeTitle + "</a>";
	}

}
