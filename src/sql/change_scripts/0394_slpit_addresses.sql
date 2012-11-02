DROP FUNCTION IF EXISTS SPLIT_STR
;

CREATE FUNCTION SPLIT_STR(X VARCHAR(255), DELIM VARCHAR(12), POS INT)
   RETURNS VARCHAR(255)
   RETURN REPLACE(
             SUBSTRING(SUBSTRING_INDEX(X, DELIM, POS),
                       LENGTH(SUBSTRING_INDEX(X, DELIM, POS - 1)) + 1),
             DELIM,
             '')
;

ALTER TABLE APPLICATION_FORM_ADDRESS
ADD COLUMN ADDRESS1 VARCHAR(50),
ADD COLUMN ADDRESS2 VARCHAR(50),
ADD COLUMN ADDRESS3 VARCHAR(50),
ADD COLUMN ADDRESS4 VARCHAR(50),
ADD COLUMN ADDRESS5 VARCHAR(12),
ADD COLUMN EMPLOYER_ID INTEGER UNSIGNED,
ADD COLUMN REFREE_ID INTEGER UNSIGNED,
MODIFY COLUMN LOCATION VARCHAR(2000),
DROP FOREIGN KEY application_form_address_fk,
DROP COLUMN APPLICATION_FORM_ID
;

UPDATE APPLICATION_FORM_ADDRESS
   SET ADDRESS1 = SPLIT_STR(LOCATION, '\n', 1),
       ADDRESS2 = SPLIT_STR(LOCATION, '\n', 2),
       ADDRESS3 = SPLIT_STR(LOCATION, '\n', 3),
       ADDRESS4 =
          TRIM(LEADING '\n' FROM TRIM(LEADING SUBSTRING_INDEX(LOCATION,
                                                              '\n',
                                                              4) FROM LOCATION)),
       LOCATION = NULL
 WHERE     LOCATION LIKE '%\n%'
       AND LENGTH(SUBSTRING_INDEX(LOCATION, '\n', 1)) < 51
       AND LENGTH(SPLIT_STR(LOCATION, '\n', 2)) < 51
       AND LENGTH(SPLIT_STR(LOCATION, '\n', 3)) < 51
       AND LENGTH(
              TRIM(LEADING '\n' FROM TRIM(LEADING SUBSTRING_INDEX(LOCATION,
                                                                  '\n',
                                                                  4) FROM LOCATION))) <
              51
;

UPDATE APPLICATION_FORM_ADDRESS
   SET ADDRESS1 = SPLIT_STR(LOCATION, ',', 1),
       ADDRESS2 = SPLIT_STR(LOCATION, ',', 2),
       ADDRESS3 = SPLIT_STR(LOCATION, ',', 3),
       ADDRESS4 =
          TRIM(LEADING ',' FROM TRIM(LEADING SUBSTRING_INDEX(LOCATION,
                                                             ',',
                                                             4) FROM LOCATION)),
       LOCATION = NULL
 WHERE     LOCATION LIKE '%,%'
       AND LENGTH(SUBSTRING_INDEX(LOCATION, ',', 1)) < 51
       AND LENGTH(SPLIT_STR(LOCATION, ',', 2)) < 51
       AND LENGTH(SPLIT_STR(LOCATION, ',', 3)) < 51
       AND LENGTH(
              TRIM(LEADING ',' FROM TRIM(LEADING SUBSTRING_INDEX(LOCATION,
                                                                 ',',
                                                                 4) FROM LOCATION))) <
              51
;

UPDATE APPLICATION_FORM_ADDRESS
   SET ADDRESS1 = SUBSTRING(LOCATION, 1, 50),
       ADDRESS2 = SUBSTRING(LOCATION, 51, 50),
       ADDRESS3 = SUBSTRING(LOCATION, 101, 50),
       ADDRESS4 = SUBSTRING(LOCATION, 151),
       LOCATION = NULL
 WHERE LOCATION IS NOT NULL
;

 ALTER TABLE APPLICATION_FORM_ADDRESS DROP COLUMN LOCATION
;
 RENAME TABLE APPLICATION_FORM_ADDRESS TO ADDRESS
;

 ALTER TABLE APPLICATION_FORM_EMPLOYMENT_POSITION
ADD COLUMN ADDRESS_ID INTEGER UNSIGNED,
ADD CONSTRAINT application_form_employment_position_address_fk FOREIGN KEY (ADDRESS_ID) REFERENCES ADDRESS(ID),
MODIFY COLUMN EMPLOYER_ADDRESS VARCHAR(1000)
;

INSERT INTO ADDRESS(EMPLOYER_ID,
                    ADDRESS1,
                    ADDRESS2,
                    ADDRESS3,
                    ADDRESS4,
                    COUNTRY_ID)
   SELECT ID,
          SPLIT_STR(EMPLOYER_ADDRESS, '\n', 1),
          SPLIT_STR(EMPLOYER_ADDRESS, '\n', 2),
          SPLIT_STR(EMPLOYER_ADDRESS, '\n', 3),
          TRIM(LEADING '\n' FROM TRIM(LEADING SUBSTRING_INDEX(
                                                 EMPLOYER_ADDRESS,
                                                 '\n',
                                                 4) FROM EMPLOYER_ADDRESS)),
          EMPLOYER_COUNTRY_ID
     FROM APPLICATION_FORM_EMPLOYMENT_POSITION
    WHERE     EMPLOYER_ADDRESS LIKE '%\n%'
          AND LENGTH(SUBSTRING_INDEX(EMPLOYER_ADDRESS, '\n', 1)) < 51
          AND LENGTH(SPLIT_STR(EMPLOYER_ADDRESS, '\n', 2)) < 51
          AND LENGTH(SPLIT_STR(EMPLOYER_ADDRESS, '\n', 3)) < 51
          AND LENGTH(
                 TRIM(LEADING '\n' FROM TRIM(LEADING SUBSTRING_INDEX(
                                                        EMPLOYER_ADDRESS,
                                                        '\n',
                                                        4) FROM EMPLOYER_ADDRESS))) <
                 51
;

INSERT INTO ADDRESS(EMPLOYER_ID,
                    ADDRESS1,
                    ADDRESS2,
                    ADDRESS3,
                    ADDRESS4,
                    COUNTRY_ID)
   SELECT ID,
          SPLIT_STR(EMPLOYER_ADDRESS, ',', 1),
          SPLIT_STR(EMPLOYER_ADDRESS, ',', 2),
          SPLIT_STR(EMPLOYER_ADDRESS, ',', 3),
          TRIM(LEADING ',' FROM TRIM(LEADING SUBSTRING_INDEX(
                                                EMPLOYER_ADDRESS,
                                                ',',
                                                4) FROM EMPLOYER_ADDRESS)),
          EMPLOYER_COUNTRY_ID
     FROM APPLICATION_FORM_EMPLOYMENT_POSITION
    WHERE     EMPLOYER_ADDRESS LIKE '%,%'
          AND NOT EXISTS
                     (SELECT 1
                        FROM ADDRESS
                       WHERE EMPLOYER_ID =
                                APPLICATION_FORM_EMPLOYMENT_POSITION.ID)
          AND LENGTH(SUBSTRING_INDEX(EMPLOYER_ADDRESS, ',', 1)) < 51
          AND LENGTH(SPLIT_STR(EMPLOYER_ADDRESS, ',', 2)) < 51
          AND LENGTH(SPLIT_STR(EMPLOYER_ADDRESS, ',', 3)) < 51
          AND LENGTH(
                 TRIM(LEADING ',' FROM TRIM(LEADING SUBSTRING_INDEX(
                                                       EMPLOYER_ADDRESS,
                                                       ',',
                                                       4) FROM EMPLOYER_ADDRESS))) <
                 51
;

INSERT INTO ADDRESS(EMPLOYER_ID,
                    ADDRESS1,
                    ADDRESS2,
                    ADDRESS3,
                    ADDRESS4,
                    COUNTRY_ID)
   SELECT ID,
          SUBSTRING(EMPLOYER_ADDRESS, 1, 50),
          SUBSTRING(EMPLOYER_ADDRESS, 51, 50),
          SUBSTRING(EMPLOYER_ADDRESS, 101, 50),
          SUBSTRING(EMPLOYER_ADDRESS, 151),
          EMPLOYER_COUNTRY_ID
     FROM APPLICATION_FORM_EMPLOYMENT_POSITION
    WHERE NOT EXISTS
             (SELECT 1
                FROM ADDRESS
               WHERE EMPLOYER_ID = APPLICATION_FORM_EMPLOYMENT_POSITION.ID)
;

UPDATE    APPLICATION_FORM_EMPLOYMENT_POSITION
       INNER JOIN
          ADDRESS
       ON APPLICATION_FORM_EMPLOYMENT_POSITION.ID = ADDRESS.EMPLOYER_ID
   SET APPLICATION_FORM_EMPLOYMENT_POSITION.ADDRESS_ID = ADDRESS.ID
;

 ALTER TABLE APPLICATION_FORM_EMPLOYMENT_POSITION DROP FOREIGN KEY employer_country_fk, DROP COLUMN EMPLOYER_COUNTRY_ID, DROP COLUMN EMPLOYER_ADDRESS
;

 ALTER TABLE APPLICATION_FORM_REFEREE
ADD COLUMN ADDRESS_ID INTEGER UNSIGNED,
ADD CONSTRAINT application_form_referee_address_fk FOREIGN KEY (ADDRESS_ID) REFERENCES ADDRESS(ID)
;

INSERT INTO ADDRESS(REFREE_ID,
                    ADDRESS1,
                    ADDRESS2,
                    ADDRESS3,
                    ADDRESS4,
                    COUNTRY_ID)
   SELECT ID,
          SPLIT_STR(ADDRESS_LOCATION, '\n', 1),
          SPLIT_STR(ADDRESS_LOCATION, '\n', 2),
          SPLIT_STR(ADDRESS_LOCATION, '\n', 3),
          TRIM(LEADING '\n' FROM TRIM(LEADING SUBSTRING_INDEX(
                                                 ADDRESS_LOCATION,
                                                 '\n',
                                                 4) FROM ADDRESS_LOCATION)),
          COUNTRY_ID
     FROM APPLICATION_FORM_REFEREE
    WHERE     ADDRESS_LOCATION LIKE '%\n%'
          AND LENGTH(SUBSTRING_INDEX(ADDRESS_LOCATION, '\n', 1)) < 51
          AND LENGTH(SPLIT_STR(ADDRESS_LOCATION, '\n', 2)) < 51
          AND LENGTH(SPLIT_STR(ADDRESS_LOCATION, '\n', 3)) < 51
          AND LENGTH(
                 TRIM(LEADING '\n' FROM TRIM(LEADING SUBSTRING_INDEX(
                                                        ADDRESS_LOCATION,
                                                        '\n',
                                                        4) FROM ADDRESS_LOCATION))) <
                 51
;

INSERT INTO ADDRESS(REFREE_ID,
                    ADDRESS1,
                    ADDRESS2,
                    ADDRESS3,
                    ADDRESS4,
                    COUNTRY_ID)
   SELECT ID,
          SPLIT_STR(ADDRESS_LOCATION, ',', 1),
          SPLIT_STR(ADDRESS_LOCATION, ',', 2),
          SPLIT_STR(ADDRESS_LOCATION, ',', 3),
          TRIM(LEADING ',' FROM TRIM(LEADING SUBSTRING_INDEX(
                                                ADDRESS_LOCATION,
                                                ',',
                                                4) FROM ADDRESS_LOCATION)),
          COUNTRY_ID
     FROM APPLICATION_FORM_REFEREE
    WHERE     ADDRESS_LOCATION LIKE '%,%'
          AND NOT EXISTS
                 (SELECT 1
                    FROM ADDRESS
                   WHERE REFREE_ID = APPLICATION_FORM_REFEREE.ID)
          AND LENGTH(SUBSTRING_INDEX(ADDRESS_LOCATION, ',', 1)) < 51
          AND LENGTH(SPLIT_STR(ADDRESS_LOCATION, ',', 2)) < 51
          AND LENGTH(SPLIT_STR(ADDRESS_LOCATION, ',', 3)) < 51
          AND LENGTH(
                 TRIM(LEADING ',' FROM TRIM(LEADING SUBSTRING_INDEX(
                                                       ADDRESS_LOCATION,
                                                       ',',
                                                       4) FROM ADDRESS_LOCATION))) <
                 51
;

INSERT INTO ADDRESS(REFREE_ID,
                    ADDRESS1,
                    ADDRESS2,
                    ADDRESS3,
                    ADDRESS4,
                    COUNTRY_ID)
   SELECT ID,
          SUBSTRING(ADDRESS_LOCATION, 1, 50),
          SUBSTRING(ADDRESS_LOCATION, 51, 50),
          SUBSTRING(ADDRESS_LOCATION, 101, 50),
          SUBSTRING(ADDRESS_LOCATION, 151),
          COUNTRY_ID
     FROM APPLICATION_FORM_REFEREE
    WHERE NOT EXISTS
             (SELECT 1
                FROM ADDRESS
               WHERE REFREE_ID = APPLICATION_FORM_REFEREE.ID)
;

UPDATE    APPLICATION_FORM_REFEREE
       INNER JOIN
          ADDRESS
       ON APPLICATION_FORM_REFEREE.ID = ADDRESS.REFREE_ID
   SET APPLICATION_FORM_REFEREE.ADDRESS_ID = ADDRESS.ID
;

 ALTER TABLE APPLICATION_FORM_REFEREE DROP FOREIGN KEY country_referee_fk, DROP COLUMN COUNTRY_ID, DROP COLUMN ADDRESS_LOCATION
;

 ALTER TABLE ADDRESS DROP COLUMN EMPLOYER_ID, DROP COLUMN REFREE_ID
;

 DROP FUNCTION SPLIT_STR
;