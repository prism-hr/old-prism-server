update notification_configuration
set subject = replace(subject, "${TEMPLATE_AUTHOR}", "${TEMPLATE_AUTHOR_FULL_NAME}"),
	content = replace(content, "${TEMPLATE_AUTHOR}", "${TEMPLATE_AUTHOR_FULL_NAME}")
where subject like "%${TEMPLATE_AUTHOR}%"
	or content like "%${TEMPLATE_AUTHOR}%"
;
