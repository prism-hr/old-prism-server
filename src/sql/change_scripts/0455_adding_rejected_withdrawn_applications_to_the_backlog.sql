INSERT INTO APPLICATION_FORM_TRANSFER (application_id, transfer_begin_timeppoint, status, ucl_user_id_received, ucl_booking_ref_number_received)
SELECT form.id, NOW(), "QUEUED_FOR_WEBSERVICE_CALL", NULL, NULL
FROM APPLICATION_FORM form 
LEFT OUTER JOIN APPLICATION_FORM_TRANSFER transfer ON form.id = transfer.application_id
WHERE (form.status = "REJECTED" OR form.status = "WITHDRAWN") AND transfer.application_id IS NULL
;
