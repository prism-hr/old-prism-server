ALTER TABLE APPLICATION_FORM_PERSONAL_DETAIL ADD COLUMN passport_available BOOLEAN DEFAULT NULL AFTER requires_visa
;

UPDATE APPLICATION_FORM_PERSONAL_DETAIL a 
INNER JOIN APPLICATION_FORM_PERSONAL_DETAIL_PASSPORT b ON a.id = b.application_form_personal_detail_id 
SET a.passport_available = true
;

UPDATE APPLICATION_FORM_PERSONAL_DETAIL  
SET passport_available = false WHERE 
requires_visa = FALSE AND passport_available IS NULL 
;
