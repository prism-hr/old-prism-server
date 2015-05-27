read -p "This script will drop the database and create it populated with test data..."

mysql --user=pgadmissions --password=pgadmissions < setup/dropdatabase.sql
mysql --user=pgadmissions --password=pgadmissions < setup/createdatabase.sql
mysql --user=pgadmissions --password=pgadmissions pgadmissions < setup/createSchemaVersionTable.mysql.sql

cd ../../
mvn clean package -Dmaven.test.skip
cd src/sql/

mysql --user=pgadmissions --password=pgadmissions pgadmissions < misc/insert_projects.sql
mysql --user=pgadmissions --password=pgadmissions pgadmissions < misc/insert_test_data.sql
mysql --user=pgadmissions --password=pgadmissions pgadmissions < misc/insert_users_to_programs.sql
mysql --user=pgadmissions --password=pgadmissions pgadmissions < misc/insertSuperAdminUser.sql
mysql --user=pgadmissions --password=pgadmissions pgadmissions < misc/update_user_details.sql
mysql --user=pgadmissions --password=pgadmissions pgadmissions < misc/updateScriptForTestingEmailReminders.sql