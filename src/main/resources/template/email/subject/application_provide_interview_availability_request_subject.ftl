[#if TEMPLATE_RECIPIENT_EMAIL?matches(APPLICATION_CREATOR_EMAIL)]
	Your Application ${APPLICATION_CODE} - Interview Scheduling Request
[#else]
	${APPLICATION_CREATOR_FULL_NAME} Application ${APPLICATION_CODE} - Interview Scheduling Request
[/#if]
