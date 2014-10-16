CREATE PROCEDURE SP_REASSIGN_UNCLASSIFIED_PROGRAM_TYPE()
BEGIN
	
	UPDATE INSTITUTION
	SET is_ucl_institution = 0
	WHERE title != "University College London";
	
	SET @institution_id = (
		SELECT id
		FROM INSTITUTION
		WHERE is_ucl_institution = 1);
		
	SET @unclassified_id = (
		SELECT id
		FROM IMPORTED_ENTITY
		WHERE institution_id = @institution_id
			AND imported_entity_type = "PROGRAM_TYPE"
			AND code = "UNCLASSIFIED");	
		
	SET @training_id = (
		SELECT id
		FROM IMPORTED_ENTITY
		WHERE institution_id = @institution_id
			AND imported_entity_type = "PROGRAM_TYPE"
			AND code = "TRAINING");
			
	UPDATE PROGRAM
	SET program_type_id = @training_id
	WHERE program_type_id = @unclassified_id;
	
	DELETE
	FROM IMPORTED_ENTITY
	WHERE id = @unclassified_id; 	
	
END
;

CALL SP_REASSIGN_UNCLASSIFIED_PROGRAM_TYPE()
;

DROP PROCEDURE SP_REASSIGN_UNCLASSIFIED_PROGRAM_TYPE
;
