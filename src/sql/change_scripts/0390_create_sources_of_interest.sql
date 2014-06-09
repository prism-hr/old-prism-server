CREATE TABLE `SOURCES_OF_INTEREST` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL DEFAULT '',
  `code` varchar(50) NOT NULL DEFAULT '',
  `enabled` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
;

INSERT INTO SOURCES_OF_INTEREST (name, code, enabled) VALUES 
("British Council", "BRIT_COUN", true), 
("Careers Centre", "CAREERS", true), 
("Employer", "EMPLOYER", true), 
("Former UCL Graduate", "UCL_GRAD", true), 
("Newspaper/Recruitment guide/Magazine advertisement", "NEWS_AD", true), 
("Other", "OTHER", true), 
("Other Academic Staff", "OTH_ACAD", true), 
("Other Website", "OTH_WEB", true), 
("Prospectus/Departmental Brochure", "PROSPECTUS", true), 
("Student Recruitment Exhibition/Fair", "STU_REC_EX", true), 
("UCL Academic Staff", "UCL_ACAD", true), 
("UCL Website", "UCL_WEB", true)
;

ALTER TABLE SOURCES_OF_INTEREST ADD INDEX sources_of_interest_code_idx (code)
;

ALTER TABLE SOURCES_OF_INTEREST ADD INDEX sources_of_interest_name_idx (name)
;

ALTER TABLE APPLICATION_FORM_PROGRAMME_DETAIL 
ADD COLUMN sources_of_interest_id INT(10) UNSIGNED NOT NULL AFTER referrer,
ADD COLUMN sources_of_interest_text VARCHAR(100) DEFAULT NULL AFTER sources_of_interest_id
;

UPDATE APPLICATION_FORM_PROGRAMME_DETAIL
SET sources_of_interest_id = (SELECT id FROM SOURCES_OF_INTEREST WHERE code = "OTHER"),
sources_of_interest_text = "UCL graduate study website"
WHERE referrer = "OPTION_1"
;

UPDATE APPLICATION_FORM_PROGRAMME_DETAIL
SET sources_of_interest_id = (SELECT id FROM SOURCES_OF_INTEREST WHERE code = "OTHER"),
sources_of_interest_text = "UCL graduate study newsletter"
WHERE referrer = "OPTION_2"
;

UPDATE APPLICATION_FORM_PROGRAMME_DETAIL
SET sources_of_interest_id = (SELECT id FROM SOURCES_OF_INTEREST WHERE code = "OTHER"),
sources_of_interest_text = "Student forum website"
WHERE referrer = "OPTION_3"
;

UPDATE APPLICATION_FORM_PROGRAMME_DETAIL
SET sources_of_interest_id = (SELECT id FROM SOURCES_OF_INTEREST WHERE code = "OTHER"),
sources_of_interest_text = "Facebook alert, friend or page"
WHERE referrer = "OPTION_4"
;

UPDATE APPLICATION_FORM_PROGRAMME_DETAIL
SET sources_of_interest_id = (SELECT id FROM SOURCES_OF_INTEREST WHERE code = "OTHER"),
sources_of_interest_text = "Facebook advert"
WHERE referrer = "OPTION_5"
;

UPDATE APPLICATION_FORM_PROGRAMME_DETAIL
SET sources_of_interest_id = (SELECT id FROM SOURCES_OF_INTEREST WHERE code = "OTHER"),
sources_of_interest_text = "Study programme webpage"
WHERE referrer = "OPTION_6"
;

UPDATE APPLICATION_FORM_PROGRAMME_DETAIL
SET sources_of_interest_id = (SELECT id FROM SOURCES_OF_INTEREST WHERE code = "OTHER"),
sources_of_interest_text = "Study programme newsletter"
WHERE referrer = "OPTION_7"
;

UPDATE APPLICATION_FORM_PROGRAMME_DETAIL
SET sources_of_interest_id = (SELECT id FROM SOURCES_OF_INTEREST WHERE code = "OTHER"),
sources_of_interest_text = "Referral by friend or colleague"
WHERE referrer = "OPTION_8"
;

UPDATE APPLICATION_FORM_PROGRAMME_DETAIL
SET sources_of_interest_id = (SELECT id FROM SOURCES_OF_INTEREST WHERE code = "OTHER"),
sources_of_interest_text = "Referral by detapartmental administrator"
WHERE referrer = "OPTION_9"
;

UPDATE APPLICATION_FORM_PROGRAMME_DETAIL
SET sources_of_interest_id = (SELECT id FROM SOURCES_OF_INTEREST WHERE code = "OTHER"),
sources_of_interest_text = "Referral by UCL tutor/researcher"
WHERE referrer = "OPTION_10"
;

UPDATE APPLICATION_FORM_PROGRAMME_DETAIL
SET sources_of_interest_id = (SELECT id FROM SOURCES_OF_INTEREST WHERE code = "OTHER"),
sources_of_interest_text = "Google advert/sponsored link"
WHERE referrer = "OPTION_11"
;

UPDATE APPLICATION_FORM_PROGRAMME_DETAIL
SET sources_of_interest_id = (SELECT id FROM SOURCES_OF_INTEREST WHERE code = "OTHER"),
sources_of_interest_text = "Google search query"
WHERE referrer = "OPTION_12"
;

UPDATE APPLICATION_FORM_PROGRAMME_DETAIL
SET sources_of_interest_id = (SELECT id FROM SOURCES_OF_INTEREST WHERE code = "OTHER"),
sources_of_interest_text = "FindAPhd.com"
WHERE referrer = "OPTION_13"
;

UPDATE APPLICATION_FORM_PROGRAMME_DETAIL
SET sources_of_interest_id = (SELECT id FROM SOURCES_OF_INTEREST WHERE code = "OTHER"),
sources_of_interest_text = "HotCourses.com"
WHERE referrer = "OPTION_14"
;

UPDATE APPLICATION_FORM_PROGRAMME_DETAIL
SET sources_of_interest_id = (SELECT id FROM SOURCES_OF_INTEREST WHERE code = "OTHER"),
sources_of_interest_text = "PostgraduateStudentships.co.uk"
WHERE referrer = "OPTION_15"
;

UPDATE APPLICATION_FORM_PROGRAMME_DETAIL
SET sources_of_interest_id = (SELECT id FROM SOURCES_OF_INTEREST WHERE code = "OTHER"),
sources_of_interest_text = "Jobs.ac.uk"
WHERE referrer = "OPTION_16"
;

UPDATE APPLICATION_FORM_PROGRAMME_DETAIL
SET sources_of_interest_id = (SELECT id FROM SOURCES_OF_INTEREST WHERE code = "OTHER"),
sources_of_interest_text = "FindAScholarship.com"
WHERE referrer = "OPTION_17"
;

UPDATE APPLICATION_FORM_PROGRAMME_DETAIL
SET sources_of_interest_id = (SELECT id FROM SOURCES_OF_INTEREST WHERE code = "OTHER"),
sources_of_interest_text = "Prospects.ac.uk"
WHERE referrer = "OPTION_18"
;

UPDATE APPLICATION_FORM_PROGRAMME_DETAIL
SET sources_of_interest_id = (SELECT id FROM SOURCES_OF_INTEREST WHERE code = "OTHER"),
sources_of_interest_text = "Prospects.ac.uk newsletter"
WHERE referrer = "OPTION_19"
;

ALTER TABLE APPLICATION_FORM_PROGRAMME_DETAIL DROP COLUMN referrer
;

ALTER TABLE APPLICATION_FORM_PROGRAMME_DETAIL 
ADD CONSTRAINT sources_of_interest_fk FOREIGN KEY (sources_of_interest_id) REFERENCES SOURCES_OF_INTEREST(id)
;
