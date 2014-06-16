SELECT GROUP_CONCAT(DISTINCT 
	STATE_ACTION.raises_urgent_flag, "|",
	STATE_ACTION.action_id
	ORDER BY STATE_ACTION.raises_urgent_flag DESC,
		STATE_ACTION.action_id)
FROM USER INNER JOIN USER_ROLE
	ON USER.id = USER_ROLE.user_id
INNER JOIN ${queryScopeUpper}
	ON USER_ROLE.${queryScopeLower}_id = ${queryScopeUpper}.id
<#list parentScopes as parentScope>
	OR USER_ROLE.${parentScope.id?lower_case}_id = ${queryScopeUpper}.${parentScope.id?lower_case}_id
</#list>
INNER JOIN STATE_ACTION_ASSIGNMENT
	ON USER_ROLE.role_id = STATE_ACTION_ASSIGNMENT.role_id
INNER JOIN STATE_ACTION
	ON STATE_ACTION_ASSIGNMENT.state_action_id = STATE_ACTION.id
INNER JOIN STATE
	ON STATE_ACTION.state_id = STATE.id
INNER JOIN ACTION
	ON STATE_ACTION.action_id = ACTION.id
WHERE USER.parent_user_id = ${user.id?c}
	AND ${queryScopeUpper}.id = ${queryScopeUpper}_LIST_BLOCK.id
	AND ${queryScopeUpper}.state_id = STATE_ACTION.state_id
	AND (STATE_ACTION.precedence IS NULL
		OR STATE_ACTION.precedence = (
			SELECT MAX(STATE_ACTION.precedence)
			FROM USER INNER JOIN USER_ROLE
				ON USER.id = USER_ROLE.user_id
			INNER JOIN ${queryScopeUpper}
				ON USER_ROLE.${queryScopeLower}_id = ${queryScopeUpper}.id
			<#list parentScopes as parentScope>
				OR USER_ROLE.${parentScope.id?lower_case}_id = ${queryScopeUpper}.${parentScope.id?lower_case}_id
			</#list>
			INNER JOIN STATE_ACTION_ASSIGNMENT
				ON USER_ROLE.role_id = STATE_ACTION_ASSIGNMENT.role_id
			INNER JOIN STATE_ACTION
				ON STATE_ACTION_ASSIGNMENT.state_action_id = STATE_ACTION.id
			INNER JOIN STATE
				ON STATE_ACTION.state_id = STATE.id
			INNER JOIN ACTION
				ON STATE_ACTION.action_id = ACTION.id
			WHERE USER.parent_user_id = ${user.id?c}
				AND ${queryScopeUpper}.id = ${queryScopeUpper}_LIST_BLOCK.id
				AND ${queryScopeUpper}.state_id = STATE_ACTION.state_id
				AND STATE_ACTION.precedence IS NOT NULL
				AND STATE.scope_id = "${queryScopeUpper}"
				AND ACTION.action_type_id = "USER_INVOCATION"))
	AND STATE.scope_id = "${queryScopeUpper}"
	AND ACTION.action_type_id = "USER_INVOCATION"