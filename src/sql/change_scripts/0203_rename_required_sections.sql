UPDATE institution
SET
  advert_incomplete_section = replace(advert_incomplete_section, "SYSTEM_RESOURCE_SUMMARY_HEADER", "RESOURCE_DETAILS"),
  advert_incomplete_section = replace(advert_incomplete_section, "SYSTEM_RESOURCE_ADVERT_HEADER", "ADVERT_DETAILS"),
  advert_incomplete_section = replace(advert_incomplete_section, "SYSTEM_RESOURCE_ADVERT_CATEGORIES_HEADER", "ADVERT_CATEGORIES"),
  advert_incomplete_section = replace(advert_incomplete_section, "SYSTEM_RESOURCE_TARGETS_HEADER", "ADVERT_TARGETS"),
  advert_incomplete_section = replace(advert_incomplete_section, "SYSTEM_RESOURCE_COMPETENCES_HEADER", "ADVERT_COMPETENCES")
;

UPDATE department
SET
  advert_incomplete_section = replace(advert_incomplete_section, "SYSTEM_RESOURCE_SUMMARY_HEADER", "RESOURCE_DETAILS"),
  advert_incomplete_section = replace(advert_incomplete_section, "SYSTEM_RESOURCE_ADVERT_HEADER", "ADVERT_DETAILS"),
  advert_incomplete_section = replace(advert_incomplete_section, "SYSTEM_RESOURCE_ADVERT_CATEGORIES_HEADER", "ADVERT_CATEGORIES"),
  advert_incomplete_section = replace(advert_incomplete_section, "SYSTEM_RESOURCE_TARGETS_HEADER", "ADVERT_TARGETS"),
  advert_incomplete_section = replace(advert_incomplete_section, "SYSTEM_RESOURCE_COMPETENCES_HEADER", "ADVERT_COMPETENCES")
;

UPDATE program
SET
  advert_incomplete_section = replace(advert_incomplete_section, "SYSTEM_RESOURCE_SUMMARY_HEADER", "RESOURCE_DETAILS"),
  advert_incomplete_section = replace(advert_incomplete_section, "SYSTEM_RESOURCE_ADVERT_HEADER", "ADVERT_DETAILS"),
  advert_incomplete_section = replace(advert_incomplete_section, "SYSTEM_RESOURCE_ADVERT_CATEGORIES_HEADER", "ADVERT_CATEGORIES"),
  advert_incomplete_section = replace(advert_incomplete_section, "SYSTEM_RESOURCE_TARGETS_HEADER", "ADVERT_TARGETS"),
  advert_incomplete_section = replace(advert_incomplete_section, "SYSTEM_RESOURCE_COMPETENCES_HEADER", "ADVERT_COMPETENCES")
;

UPDATE project
SET
  advert_incomplete_section = replace(advert_incomplete_section, "SYSTEM_RESOURCE_SUMMARY_HEADER", "RESOURCE_DETAILS"),
  advert_incomplete_section = replace(advert_incomplete_section, "SYSTEM_RESOURCE_ADVERT_HEADER", "ADVERT_DETAILS"),
  advert_incomplete_section = replace(advert_incomplete_section, "SYSTEM_RESOURCE_ADVERT_CATEGORIES_HEADER", "ADVERT_CATEGORIES"),
  advert_incomplete_section = replace(advert_incomplete_section, "SYSTEM_RESOURCE_TARGETS_HEADER", "ADVERT_TARGETS"),
  advert_incomplete_section = replace(advert_incomplete_section, "SYSTEM_RESOURCE_COMPETENCES_HEADER", "ADVERT_COMPETENCES")
;


