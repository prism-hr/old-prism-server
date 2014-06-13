<#assign queryScopeLower = queryScope?lower_case>
<#assign queryScopeUpper = queryScope?upper_case>

SELECT ${queryScopeUpper}_LIST_BLOCK.*, (
	SELECT GROUP_CONCAT(DISTINCT 
		STATE_ACTION.raises_urgent_flag, "|",
		STATE_ACTION.action_id,
		IF(STATE_action.precedence IS NOT NULL,
			CONCAT("|", STATE_ACTION.precedence),
			"")
		ORDER BY STATE_ACTION.raises_urgent_flag DESC,
			STATE_ACTION.action_id
		SEPARATOR ", ")
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
		ON ${queryScopeUpper}.state_id = STATE_ACTION.state_id
		AND STATE_ACTION_ASSIGNMENT.state_action_id = STATE_ACTION.id
	INNER JOIN STATE
		ON STATE_ACTION.state_id = STATE.id
	INNER JOIN ACTION
		ON STATE_ACTION.action_id = ACTION.id
	WHERE USER.parent_user_id = ${user.id?c}
		AND ${queryScopeUpper}.id = ${queryScopeUpper}_LIST_BLOCK.id
		AND STATE.scope_id = "${queryScopeUpper}"
		AND ACTION.action_type_id = "USER_INVOCATION") 
	AS actionList
FROM (
	SELECT ${queryScopeUpper}.id AS id, ${queryScopeUpper}.code AS code,
		${queryScopeUpper}_LIST_PERMISSION.raises_urgent_flag AS raisesUrgentFlag, STATE.parent_state_id AS state,
  	<#include "${queryScopeLower}_custom_columns.ftl">
	FROM ${queryScopeUpper} INNER JOIN (
		SELECT USER_ROLE.${queryScopeLower}_id,
		<#list parentScopes as parentScope>
			USER_ROLE.${parentScope.id?lower_case}_id,
		</#list>
			STATE_ACTION.state_id, MAX(STATE_ACTION.raises_urgent_flag) AS raises_urgent_flag
		FROM USER INNER JOIN USER_ROLE
			ON USER.id = USER_ROLE.user_id
		INNER JOIN USER_ACCOUNT
			ON USER.user_account_id = USER_ACCOUNT.id
		INNER JOIN STATE_ACTION_ASSIGNMENT
			ON USER_ROLE.role_id = STATE_ACTION_ASSIGNMENT.role_id
		INNER JOIN STATE_ACTION
			ON STATE_ACTION_ASSIGNMENT.state_action_id = STATE_ACTION.id
		INNER JOIN STATE
			ON STATE_ACTION.state_id = STATE.id
		LEFT JOIN ${queryScopeUpper}
			ON USER_ROLE.${queryScopeLower}_id = ${queryScopeUpper}.id
		WHERE USER.parent_user_id = ${user.id?c}
			AND USER_ACCOUNT.enabled = 1
			AND STATE.scope_id = "${queryScopeUpper}"
			AND (USER_ROLE.${queryScopeLower}_id IS NULL
				OR (${queryScopeUpper}.updated_timestamp >= CURRENT_TIMESTAMP - INTERVAL ${queryRangeValue} ${queryRangeUnit}
					AND ${queryScopeUpper}.state_id = STATE.id))
		GROUP BY USER_ROLE.${queryScopeLower}_id, STATE_ACTION.state_id,
		<#list parentScopes as parentScope>
			USER_ROLE.${parentScope.id?lower_case}_id, STATE_ACTION.state_id<#if parentScope_has_next>,<#else>)</#if>
		</#list>
		AS ${queryScopeUpper}_LIST_PERMISSION
		ON ${queryScopeUpper}.state_id = ${queryScopeUpper}_LIST_PERMISSION.state_id
		AND (${queryScopeUpper}.id = ${queryScopeUpper}_LIST_PERMISSION.${queryScopeLower}_id
		<#list parentScopes as parentScope>
			OR ${queryScopeUpper}.${parentScope.id?lower_case}_id = ${queryScopeUpper}_LIST_PERMISSION.${parentScope.id?lower_case}_id<#if !parentScope_has_next>)</#if>
		</#list>
  	<#if queryScopeUpper == "APPLICATION">
  		<#include "${queryScopeLower}_custom_joins.ftl">
  	</#if>
	INNER JOIN STATE
		ON ${queryScopeUpper}.state_id = STATE.id
	WHERE ${queryScopeUpper}.updated_timestamp >= CURRENT_TIMESTAMP - INTERVAL ${queryRangeValue} ${queryRangeUnit}
	GROUP BY ${queryScopeUpper}.id
	ORDER BY ${queryScopeUpper}_LIST_PERMISSION.raises_urgent_flag DESC, ${queryScopeUpper}.updated_timestamp DESC
	LIMIT ${rowIndex}, ${rowCount}) 
AS ${queryScopeUpper}_LIST_BLOCK
