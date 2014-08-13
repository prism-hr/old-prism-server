INNER JOIN CONFIGURATION
	ON (${queryScopeUpper}.program_id = CONFIGURATION.program_id
	OR (${queryScopeUpper}.institution_id = CONFIGURATION.institution_id
		AND CONFIGURATION.program_id IS NULL)
	OR (${queryScopeUpper}.system_id = CONFIGURATION.system_id
		AND CONFIGURATION.program_id IS NULL
		AND CONFIGURATION.institution_id IS NULL))
	AND CONFIGURATION.configuration_parameter = "DAY_ACTION_EXPIRY_DURATION"