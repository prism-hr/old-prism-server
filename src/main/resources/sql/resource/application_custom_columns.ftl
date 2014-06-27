USER.first_name AS creatorFirstName, USER.first_name_2 AS creatorFirstName2, 
USER.first_name_3 AS creatorFirstName3, USER.last_name AS creatorLastName,
USER.email AS creatorEmail,
NULL as institutionTitle,
PROGRAM.title AS programTitle,
PROJECT.title AS projectTitle,
IF(APPLICATION.submitted_timestamp IS NOT NULL,
	APPLICATION.submitted_timestamp,
	APPLICATION.created_timestamp) AS displayTimestamp
