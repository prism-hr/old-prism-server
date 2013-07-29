SELECT REGISTERED_USER.id
FROM APPLICATION_FORM INNER JOIN REGISTERED_USER 
ON APPLICATION_FORM.app_administrator_id = REGISTERED_USER.id
WHERE APPLICATION_FORM.status IN ("VALIDATION", "REVIEW", "INTERVIEW", "APPROVAL")
AND (REGISTERED_USER.latest_task_notification_date IS NULL
OR DATE(REGISTERED_USER.latest_task_notification_date) < CURRENT_DATE() - INTERVAL :intervalDays DAY)
AND REGISTERED_USER.accountNonExpired = 1
AND REGISTERED_USER.accountNonLocked = 1
GROUP BY REGISTERED_USER.id
UNION
SELECT REGISTERED_USER.id
FROM APPLICATION_FORM INNER JOIN PROGRAM_ADMINISTRATOR_LINK
ON APPLICATION_FORM.program_id = PROGRAM_ADMINISTRATOR_LINK.program_id
INNER JOIN REGISTERED_USER 
ON PROGRAM_ADMINISTRATOR_LINK.administrator_id = REGISTERED_USER.id
WHERE APPLICATION_FORM.status IN ("VALIDATION", "REVIEW", "INTERVIEW", "APPROVAL")
AND (DATE(REGISTERED_USER.latest_task_notification_date) IS NULL
OR DATE(REGISTERED_USER.latest_task_notification_date) < CURRENT_DATE() - INTERVAL :intervalDays DAY)
AND REGISTERED_USER.accountNonExpired = 1
AND REGISTERED_USER.accountNonLocked = 1
GROUP BY REGISTERED_USER.id
UNION
SELECT REGISTERED_USER.id
FROM APPLICATION_FORM INNER JOIN REVIEWER
ON APPLICATION_FORM.latest_review_round_id = REVIEWER.review_round_id
INNER JOIN REGISTERED_USER 
ON REVIEWER.registered_user_id = REGISTERED_USER.id
WHERE APPLICATION_FORM.status = "REVIEW"
AND (DATE(REGISTERED_USER.latest_task_notification_date) IS NULL
OR DATE(REGISTERED_USER.latest_task_notification_date) < CURRENT_DATE() - INTERVAL :intervalDays DAY)
AND REGISTERED_USER.accountNonExpired = 1
AND REGISTERED_USER.accountNonLocked = 1
GROUP BY REGISTERED_USER.id
UNION
SELECT REGISTERED_USER.id
FROM APPLICATION_FORM INNER JOIN INTERVIEW
ON APPLICATION_FORM.latest_interview_id = INTERVIEW.id
INNER JOIN interviewer
ON INTERVIEW.id = interviewer.interview_id
INNER JOIN REGISTERED_USER 
ON interviewer.registered_user_id = REGISTERED_USER.id
WHERE APPLICATION_FORM.status = "INTERVIEW"
AND INTERVIEW.stage = "SCHEDULED"
AND (DATE(REGISTERED_USER.latest_task_notification_date) IS NULL
OR DATE(REGISTERED_USER.latest_task_notification_date) < CURRENT_DATE() - INTERVAL :intervalDays DAY)
AND REGISTERED_USER.accountNonExpired = 1
AND REGISTERED_USER.accountNonLocked = 1
GROUP BY REGISTERED_USER.id
UNION
SELECT REGISTERED_USER.id
FROM APPLICATION_FORM INNER JOIN SUPERVISOR
ON APPLICATION_FORM.latest_approval_round_id = SUPERVISOR.approval_round_id
INNER JOIN REGISTERED_USER 
ON SUPERVISOR.registered_user_id = REGISTERED_USER.id
WHERE APPLICATION_FORM.status = "APPROVAL"
AND (DATE(REGISTERED_USER.latest_task_notification_date) IS NULL
OR DATE(REGISTERED_USER.latest_task_notification_date) < CURRENT_DATE() - INTERVAL :intervalDays DAY)
AND REGISTERED_USER.accountNonExpired = 1
AND REGISTERED_USER.accountNonLocked = 1
GROUP BY REGISTERED_USER.id
UNION
SELECT REGISTERED_USER.id
FROM APPLICATION_FORM INNER JOIN PROGRAM_APPROVER_LINK
ON APPLICATION_FORM.program_id = PROGRAM_APPROVER_LINK.program_id
INNER JOIN REGISTERED_USER 
ON PROGRAM_APPROVER_LINK.registered_user_id = REGISTERED_USER.id
WHERE APPLICATION_FORM.status = "APPROVAL"
AND (DATE(REGISTERED_USER.latest_task_notification_date) IS NULL
OR DATE(REGISTERED_USER.latest_task_notification_date) < CURRENT_DATE() - INTERVAL :intervalDays DAY)
AND REGISTERED_USER.accountNonExpired = 1
AND REGISTERED_USER.accountNonLocked = 1
GROUP BY REGISTERED_USER.id
UNION
SELECT REGISTERED_USER.id
FROM APPLICATION_FORM INNER JOIN USER_ROLE_LINK 
ON 1 = 1
INNER JOIN REGISTERED_USER
ON USER_ROLE_LINK.registered_user_id = REGISTERED_USER.id
WHERE APPLICATION_FORM.status IN ("APPROVED", "REJECTED", "WITHDRAWN")
AND APPLICATION_FORM.withdrawn_before_submit = 0
AND (DATE(REGISTERED_USER.latest_task_notification_date) IS NULL
OR DATE(REGISTERED_USER.latest_task_notification_date) < CURRENT_DATE() - INTERVAL :intervalDays DAY)
AND REGISTERED_USER.accountNonExpired = 1
AND REGISTERED_USER.accountNonLocked = 1
AND USER_ROLE_LINK.application_role_id = 5
GROUP BY REGISTERED_USER.id
UNION
SELECT REGISTERED_USER.id
FROM APPLICATION_FORM INNER JOIN USER_ROLE_LINK 
ON 1 = 1
INNER JOIN REGISTERED_USER
ON USER_ROLE_LINK.registered_user_id = REGISTERED_USER.id
WHERE APPLICATION_FORM.status IN ("REVIEW", "INTERVIEW", "APPROVAL")
AND (DATE(REGISTERED_USER.latest_task_notification_date) IS NULL
OR DATE(REGISTERED_USER.latest_task_notification_date) < CURRENT_DATE() - INTERVAL :intervalDays DAY)
AND REGISTERED_USER.accountNonExpired = 1
AND REGISTERED_USER.accountNonLocked = 1
AND USER_ROLE_LINK.application_role_id = 10
GROUP BY REGISTERED_USER.id;
