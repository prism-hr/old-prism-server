UPDATE IMPORTED_ENTITY
SET name = REPLACE(REPLACE(name, "\t", ""), "\n", "")
;

UPDATE IMPORTED_INSTITUTION
SET name = REPLACE(REPLACE(name, "\t", ""), "\n", "")
;

UPDATE IMPORTED_LANGUAGE_QUALIFICATION_TYPE
SET name = REPLACE(REPLACE(name, "\t", ""), "\n", "")
;
