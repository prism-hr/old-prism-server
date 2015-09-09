update institution
set advert_incomplete_section = replace(advert_incomplete_section, "|ADVERT_COMPETENCES|", "|")
;

update institution
set advert_incomplete_section = replace(advert_incomplete_section, "|ADVERT_COMPETENCES", "")
;

update department
set advert_incomplete_section = replace(advert_incomplete_section, "|ADVERT_COMPETENCES|", "|")
;

update department
set advert_incomplete_section = replace(advert_incomplete_section, "|ADVERT_COMPETENCES", "")
;
