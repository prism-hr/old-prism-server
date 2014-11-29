INSERT INTO APPLICATION_FORM_ADDITIONAL_INFO 
   (application_form_id, info_text, has_convictions)
   SELECT id, additional_information, '0'
     FROM APPLICATION_FORM
    WHERE additional_information IS NOT NULL
;