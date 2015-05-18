update application_personal_detail
set skype = null
where length(skype) = 0
;
