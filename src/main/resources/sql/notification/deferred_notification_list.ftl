<#assign queryScopeLower = queryScope?lower_case>
<#assign queryScopeUpper = queryScope?upper_case>
<@compress single_line = true>
SELECT USER_ROLE.id AS userRoleId, 
	NOTIFICATION_TEMPLATE.id AS notificationTemplateId,
	CONFIGURATION.parameter_value AS actionExpiryDuration,
	NOTIFICATION_CONFIGURATION.day_reminder_interval AS reminderInterval 
FROM ${queryScopeUpper} INNER JOIN USER_NOTIFICATION
	ON ${queryScopeUpper}.system_id = USER_NOTIFICATION.system_id
INNER JOIN NOTIFICATION_TEMPLATE
	ON USER_NOTIFICATION.notification_template_id = NOTIFICATION_TEMPLATE.id
INNER JOIN USER_ROLE
	ON USER_NOTIFICATION.user_role_id = USER_ROLE.id
INNER JOIN USER
	ON USER_ROLE.user_id = USER.id
LEFT JOIN USER_ACCOUNT
	ON USER.user_account_id = USER_ACCOUNT.id
INNER JOIN STATE_ACTION_ASSIGNMENT
	ON USER_ROLE.role_id = STATE_ACTION_ASSIGNMENT.role_id
	AND (USER_ROLE.${queryScopeLower}_id = ${queryScopeUpper}.id
		<#list parentScopes as parentScope>
			USER_ROLE.${parentScope.id?lower_case}_id = ${queryScopeUpper}.${parentScope.id?lower_case}_id
		</#list>
INNER JOIN STATE_ACTION
	ON STATE_ACTION_ASSIGNMENT.state_action_id = STATE_ACTION.id
INNER JOIN NOTIFICATION_CONFIGURATION
	ON ${queryScopeUpper}.program_id = NOTIFICATION_CONFIGURATION.program_id
	OR (${queryScopeUpper}.institution_id = NOTIFICATION_CONFIGURATION.institution_id
		AND CONFIGURATION.program_id IS NULL)
	OR (${queryScopeUpper}.system_id = NOTIFICATION_CONFIGURATION.system_id
		AND CONFIGURATION.program_id IS NULL
		AND CONFIGURATION.institution_id IS NULL)
<#include "../action_expiry_duration_join.sql">
WHERE (NOTIFICATION_TEMPLATE.notification_purpose = "UPDATE"
		AND ${queryScopeUpper}.updated_timestamp BETWEEN ${updateRangeStart} AND ${updateRangeClose})
	OR (NOTIFICATION_TEMPLATE.notification_purpose = "REQUEST"
		AND STATE_ACTION.raises_urgent_flag = 1)
	AND NOTIFICATION_TEMPLATE.scope_id = "${queryScopeUpper}"
	AND (USER.user_account_id IS NULL
		OR USER_ACCOUNT.enabled = 1)
	AND (USER_NOTIFICATION.created_date IS NULL
		OR DATEDIFF(${updateRangeStart}, USER_NOTIFICATION.created_date) = NOTIFICATION_CONFIGURATION.day_reminder_interval)
	AND DATEDIFF(CURRENT_DATE(), ${queryScopeUpper}.updated_timestamp) < CONFIGURATION.parameter_value
GROUP BY USER_ROLE.id, NOTIFICATION_TEMPLATE.id
ORDER BY NOTIFICATION_TEMPLATE.notification_purpose,
	<#list parentScopes as parentScope>
		USER_NOTIFICATION.${parentScope.id?lower_case}_id DESC,
	</#list>
	USER_NOTIFICATION.${queryScopeLower}_id DESC;
</@compress>
