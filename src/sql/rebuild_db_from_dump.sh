read -p "This script will drop the database and create it populated with provided test data..."

mysql --user=pgadmissions --password=pgadmissions < setup/dropdatabase.sql
mysql --user=pgadmissions --password=pgadmissions < setup/createdatabase.sql

mysql --user=pgadmissions --password=pgadmissions pgadmissions < $1

cd ../../
mvn clean package dbdeploy:update -DskipTests
