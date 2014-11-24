UPDATE EMAIL_TEMPLATE SET subject = '%3$s %4$s Application %1$s for UCL %2$s - Reference Request' WHERE name = 'REFEREE_NOTIFICATION'
;

UPDATE EMAIL_TEMPLATE SET subject = 'Application %1$s for UCL %2$s' WHERE name = 'APPLICATION_SUBMIT_CONFIRMATION'
;

UPDATE EMAIL_TEMPLATE SET subject = 'Application %1$s for UCL %2$s - %5$s Outcome' WHERE name = 'REJECTED_NOTIFICATION'
;

UPDATE EMAIL_TEMPLATE SET subject = 'Application %1$s for UCL %2$s - Approval Outcome' WHERE name = 'MOVED_TO_APPROVED_NOTIFICATION'
;

UPDATE EMAIL_TEMPLATE SET subject = '%3$s %4$s Application %1$s for UCL %2$s - Interview Confirmation' WHERE name = 'INTERVIEWER_NOTIFICATION'
;

UPDATE EMAIL_TEMPLATE SET subject = 'Application %1$s for UCL %2$s - Interview Confirmation' WHERE name = 'MOVED_TO_INTERVIEW_NOTIFICATION'
;

UPDATE EMAIL_TEMPLATE SET subject = 'UCL Prism to Portico Export Error' WHERE name = 'EXPORT_ERROR'
;

UPDATE EMAIL_TEMPLATE SET subject = 'UCL Prism Reference Data Import Error' WHERE name = 'IMPORT_ERROR'
;

UPDATE EMAIL_TEMPLATE SET subject = 'Your Registration for UCL Prism' WHERE name = 'REGISTRATION_CONFIRMATION'
;

UPDATE EMAIL_TEMPLATE SET subject = 'New Password for UCL Prism' WHERE name = 'NEW_PASSWORD_CONFIRMATION'
;

UPDATE EMAIL_TEMPLATE SET subject = 'REMINDER: %3$s %4$s Application %1$s for UCL %2$s - Reference Request' WHERE name = 'REFEREE_REMINDER'
;

UPDATE EMAIL_TEMPLATE SET subject = 'Application %1$s for UCL %2$s - Validation Request' WHERE name = 'REGISTRY_VALIDATION_REQUEST'
;

UPDATE EMAIL_TEMPLATE SET subject = 'Prism Digest Update Notification' WHERE name = 'DIGEST_UPDATE_NOTIFICATION'
;

UPDATE EMAIL_TEMPLATE SET subject = 'Prism Digest Task Notification' WHERE name = 'DIGEST_TASK_NOTIFICATION'
;

UPDATE EMAIL_TEMPLATE SET subject = 'Prism Digest Task Reminder' WHERE name = 'DIGEST_TASK_REMINDER'
;
