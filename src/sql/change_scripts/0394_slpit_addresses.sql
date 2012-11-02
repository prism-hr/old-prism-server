CREATE FUNCTION SPLIT_STR(x VARCHAR(255), delim VARCHAR(12), pos INT)
   RETURNS VARCHAR(255)
   RETURN REPLACE(
             SUBSTRING(SUBSTRING_INDEX(x, delim, pos),
                       LENGTH(SUBSTRING_INDEX(x, delim, pos - 1)) + 1),
             delim,
             '')
;

ALTER TABLE application_form_address
ADD COLUMN address1 varchar(50),
ADD COLUMN address2 varchar(50),
ADD COLUMN address3 varchar(50),
ADD COLUMN address4 varchar(50),
ADD COLUMN address5 varchar(12),
ADD COLUMN employer_id INTEGER UNSIGNED,
ADD COLUMN refree_id INTEGER UNSIGNED,
MODIFY COLUMN location varchar(2000),
DROP FOREIGN KEY application_form_address_fk,
DROP COLUMN application_form_id
;

UPDATE application_form_address
   SET address1 = SPLIT_STR(location, '\n', 1),
       address2 = SPLIT_STR(location, '\n', 2),
       address3 = SPLIT_STR(location, '\n', 3),
       address4 =
          trim(LEADING '\n' FROM trim(LEADING substring_index(location,
                                                              '\n',
                                                              4) FROM location)),
       location = NULL
 WHERE     location LIKE '%\n%'
       AND length(substring_index(location, '\n', 1)) < 51
       AND length(SPLIT_STR(location, '\n', 2)) < 51
       AND length(SPLIT_STR(location, '\n', 3)) < 51
       AND length(
              trim(LEADING '\n' FROM trim(LEADING substring_index(location,
                                                                  '\n',
                                                                  4) FROM location))) <
              51
;

UPDATE application_form_address
   SET address1 = SPLIT_STR(location, ',', 1),
       address2 = SPLIT_STR(location, ',', 2),
       address3 = SPLIT_STR(location, ',', 3),
       address4 =
          trim(LEADING ',' FROM trim(LEADING substring_index(location,
                                                             ',',
                                                             4) FROM location)),
       location = NULL
 WHERE     location LIKE '%,%'
       AND length(substring_index(location, ',', 1)) < 51
       AND length(SPLIT_STR(location, ',', 2)) < 51
       AND length(SPLIT_STR(location, ',', 3)) < 51
       AND length(
              trim(LEADING ',' FROM trim(LEADING substring_index(location,
                                                                 ',',
                                                                 4) FROM location))) <
              51
;

UPDATE application_form_address
   SET address1 = substring(location, 1, 50),
       address2 = substring(location, 51, 50),
       address3 = substring(location, 101, 50),
       address4 = substring(location, 151),
       location = NULL
 WHERE location IS NOT NULL
;

 ALTER TABLE application_form_address DROP COLUMN location
;
 RENAME TABLE application_form_address TO address
;

 ALTER TABLE application_form_employment_position
ADD COLUMN address_id INTEGER UNSIGNED,
ADD CONSTRAINT application_form_employment_position_address_fk FOREIGN KEY (address_id) REFERENCES address(id),
MODIFY COLUMN employer_address varchar(1000)
;

INSERT INTO address(employer_id,
                    address1,
                    address2,
                    address3,
                    address4,
                    country_id)
   SELECT id,
          SPLIT_STR(employer_address, '\n', 1),
          SPLIT_STR(employer_address, '\n', 2),
          SPLIT_STR(employer_address, '\n', 3),
          trim(LEADING '\n' FROM trim(LEADING substring_index(
                                                 employer_address,
                                                 '\n',
                                                 4) FROM employer_address)),
          employer_country_id
     FROM application_form_employment_position
    WHERE     employer_address LIKE '%\n%'
          AND length(substring_index(employer_address, '\n', 1)) < 51
          AND length(SPLIT_STR(employer_address, '\n', 2)) < 51
          AND length(SPLIT_STR(employer_address, '\n', 3)) < 51
          AND length(
                 trim(LEADING '\n' FROM trim(LEADING substring_index(
                                                        employer_address,
                                                        '\n',
                                                        4) FROM employer_address))) <
                 51
;

INSERT INTO address(employer_id,
                    address1,
                    address2,
                    address3,
                    address4,
                    country_id)
   SELECT id,
          SPLIT_STR(employer_address, ',', 1),
          SPLIT_STR(employer_address, ',', 2),
          SPLIT_STR(employer_address, ',', 3),
          trim(LEADING ',' FROM trim(LEADING substring_index(
                                                employer_address,
                                                ',',
                                                4) FROM employer_address)),
          employer_country_id
     FROM application_form_employment_position
    WHERE     employer_address LIKE '%,%'
          AND NOT EXISTS
                     (SELECT 1
                        FROM address
                       WHERE employer_id =
                                application_form_employment_position.id)
          AND length(substring_index(employer_address, ',', 1)) < 51
          AND length(SPLIT_STR(employer_address, ',', 2)) < 51
          AND length(SPLIT_STR(employer_address, ',', 3)) < 51
          AND length(
                 trim(LEADING ',' FROM trim(LEADING substring_index(
                                                       employer_address,
                                                       ',',
                                                       4) FROM employer_address))) <
                 51
;

INSERT INTO address(employer_id,
                    address1,
                    address2,
                    address3,
                    address4,
                    country_id)
   SELECT id,
          substring(employer_address, 1, 50),
          substring(employer_address, 51, 50),
          substring(employer_address, 101, 50),
          substring(employer_address, 151),
          employer_country_id
     FROM application_form_employment_position
    WHERE NOT EXISTS
             (SELECT 1
                FROM address
               WHERE employer_id = application_form_employment_position.id)
;

UPDATE    application_form_employment_position
       INNER JOIN
          address
       ON application_form_employment_position.id = address.employer_id
   SET application_form_employment_position.address_id = address.id
;

 ALTER TABLE application_form_employment_position DROP FOREIGN KEY employer_country_fk, DROP COLUMN employer_country_id, DROP COLUMN employer_address
;

 ALTER TABLE application_form_referee
ADD COLUMN address_id INTEGER UNSIGNED,
ADD CONSTRAINT application_form_referee_address_fk FOREIGN KEY (address_id) REFERENCES address(id)
;

INSERT INTO address(refree_id,
                    address1,
                    address2,
                    address3,
                    address4,
                    country_id)
   SELECT id,
          SPLIT_STR(address_location, '\n', 1),
          SPLIT_STR(address_location, '\n', 2),
          SPLIT_STR(address_location, '\n', 3),
          trim(LEADING '\n' FROM trim(LEADING substring_index(
                                                 address_location,
                                                 '\n',
                                                 4) FROM address_location)),
          country_id
     FROM application_form_referee
    WHERE     address_location LIKE '%\n%'
          AND length(substring_index(address_location, '\n', 1)) < 51
          AND length(SPLIT_STR(address_location, '\n', 2)) < 51
          AND length(SPLIT_STR(address_location, '\n', 3)) < 51
          AND length(
                 trim(LEADING '\n' FROM trim(LEADING substring_index(
                                                        address_location,
                                                        '\n',
                                                        4) FROM address_location))) <
                 51
;

INSERT INTO address(refree_id,
                    address1,
                    address2,
                    address3,
                    address4,
                    country_id)
   SELECT id,
          SPLIT_STR(address_location, ',', 1),
          SPLIT_STR(address_location, ',', 2),
          SPLIT_STR(address_location, ',', 3),
          trim(LEADING ',' FROM trim(LEADING substring_index(
                                                address_location,
                                                ',',
                                                4) FROM address_location)),
          country_id
     FROM application_form_referee
    WHERE     address_location LIKE '%,%'
          AND NOT EXISTS
                 (SELECT 1
                    FROM address
                   WHERE refree_id = application_form_referee.id)
          AND length(substring_index(address_location, ',', 1)) < 51
          AND length(SPLIT_STR(address_location, ',', 2)) < 51
          AND length(SPLIT_STR(address_location, ',', 3)) < 51
          AND length(
                 trim(LEADING ',' FROM trim(LEADING substring_index(
                                                       address_location,
                                                       ',',
                                                       4) FROM address_location))) <
                 51
;

INSERT INTO address(refree_id,
                    address1,
                    address2,
                    address3,
                    address4,
                    country_id)
   SELECT id,
          substring(address_location, 1, 50),
          substring(address_location, 51, 50),
          substring(address_location, 101, 50),
          substring(address_location, 151),
          country_id
     FROM application_form_referee
    WHERE NOT EXISTS
             (SELECT 1
                FROM address
               WHERE refree_id = application_form_referee.id)
;

UPDATE    application_form_referee
       INNER JOIN
          address
       ON application_form_referee.id = address.refree_id
   SET application_form_referee.address_id = address.id
;

 ALTER TABLE application_form_referee DROP FOREIGN KEY country_referee_fk, DROP COLUMN country_id, DROP COLUMN address_location
;

 ALTER TABLE address DROP COLUMN employer_id, DROP COLUMN refree_id
;

 DROP FUNCTION SPLIT_STR
;