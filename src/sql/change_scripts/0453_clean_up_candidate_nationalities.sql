ALTER TABLE APPLICATION_FORM_PERSONAL_DETAIL ADD COLUMN first_nationality INT(10) DEFAULT -1 not null 
;
ALTER TABLE APPLICATION_FORM_PERSONAL_DETAIL ADD COLUMN second_nationality INT(10) DEFAULT NULL 
;
CREATE PROCEDURE clean_up_nationalities()
BEGIN
	DECLARE done INT DEFAULT 0;
	DECLARE personal_detail_id INT(10);
	DECLARE nation INT(10);
	DECLARE previous_personal_detail_id INT(10) DEFAULT -1;	
	-- sort the results, making sure that entry from the same candidate are next to each other
	DECLARE link_table_cursor CURSOR FOR SELECT candidate_personal_details_id,candidate_language_id FROM CANDIDATE_NATIONALITY_LINK order by candidate_personal_details_id;
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
	OPEN link_table_cursor;
	process_loop: LOOP 
	    FETCH link_table_cursor into personal_detail_id,nation; 
		-- done is set to 1 when fetching returns no record, so the done check needs to be done after fetch
		IF done THEN
			LEAVE process_loop;
		END IF;		
		-- process for the fisrt time, update first_nationality field
		IF personal_detail_id!=previous_personal_detail_id THEN 
			UPDATE APPLICATION_FORM_PERSONAL_DETAIL SET first_nationality =nation WHERE id=personal_detail_id;
		-- process for the second time, update second_nationality field
		ELSE
			UPDATE APPLICATION_FORM_PERSONAL_DETAIL SET second_nationality=nation WHERE id=personal_detail_id;
		END IF;
		SET previous_personal_detail_id=personal_detail_id;
	END LOOP;
	CLOSE link_table_cursor;
END
;
CALL clean_up_nationalities()
;
DROP PROCEDURE `clean_up_nationalities`
;