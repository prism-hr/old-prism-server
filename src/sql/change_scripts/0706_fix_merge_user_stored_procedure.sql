DROP PROCEDURE SP_MERGE_ENTITIES
;

CREATE PROCEDURE SP_MERGE_USER (
	IN in_merge_from_user_id INT, 
	IN in_merge_into_user_id INT)
BEGIN

	DECLARE iter INT DEFAULT 1;
	DECLARE relation_count INT DEFAULT 0;
	
	CREATE TEMPORARY TABLE FOUND_RELATION (
		id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
		table_name VARCHAR(50) NOT NULL,
		column_name VARCHAR(50) NOT NULL,
		PRIMARY KEY (id)
	) ENGINE = MEMORY;
	
	START TRANSACTION;

		INSERT INTO FOUND_RELATION(table_name, column_name)
			SELECT cu.TABLE_NAME AS table_name,
				cu.COLUMN_NAME AS column_name
			FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE cu
			WHERE cu.CONSTRAINT_SCHEMA = "PGADMISSIONS"
				AND cu.REFERENCED_TABLE_NAME = "USER";
				
		SET relation_count = (select count(*) from FOUND_RELATION);

		WHILE iter <= relation_count DO

			SELECT table_name, column_name
				INTO @table_name, @column_name
			FROM FOUND_RELATION
			WHERE id = iter;
			
			SET @update_keys_statement = CONCAT('UPDATE IGNORE ', @table_name, ' SET ', @column_name, ' = ', in_merge_into_user_id, ' WHERE ', @column_name, ' = ', in_merge_from_user_id);
			PREPARE update_keys FROM @update_keys_statement;
			EXECUTE update_keys;

			SET @delete_keys_statement = CONCAT('DELETE FROM ', @table_name, ' WHERE ', @column_name, ' = ', in_merge_from_user_id);
			PREPARE delete_keys FROM @delete_keys_statement;
			EXECUTE delete_keys;

			SET iter = iter + 1;

		END WHILE;
		
		DELETE USER.*, USER_ACCOUNT.*
		FROM USER LEFT JOIN USER_ACCOUNT
			ON USER.user_account_id = USER_ACCOUNT.id
		WHERE USER.id = in_merge_from_user_id;
		
	COMMIT;

	DROP TABLE FOUND_RELATION;

END
;
