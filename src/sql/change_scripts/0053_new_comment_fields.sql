alter table comment
    drop column application_identified,
    modify column application_eligible varchar(10) after application_on_course,
    add column application_applicant_known int(1) unsigned after application_eligible,
    add column application_applicant_known_duration int(5) unsigned after application_applicant_known,
    add column application_applicant_known_capacity text after application_applicant_known_duration
;
