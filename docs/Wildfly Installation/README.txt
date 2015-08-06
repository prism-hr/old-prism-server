 _____  ____                              
/\___ \/\  _`\                            
\/__/\ \ \ \L\ \    ___     ____    ____  
   _\ \ \ \  _ <'  / __`\  /',__\  /',__\ 
  /\ \_\ \ \ \L\ \/\ \L\ \/\__, `\/\__, `\
  \ \____/\ \____/\ \____/\/\____/\/\____/
   \/___/  \/___/  \/___/  \/___/  \/___/ 
                                          
   Installation Guide

========================================================================
            Install the MySQL Driver & Connection in JBoss
========================================================================

Install the MySQL Java connector as a JBoss dependency by unzipping the "wildfly-x.x.x-mysql-modules.zip" provided in this directory to your $JBOSS_HOME

Add the following to your JBoss configuration:

    <subsystem xmlns="urn:jboss:domain:datasources:1.0">
        <datasources>
            <datasource jta="true" jndi-name="java:jboss/datasources/pgadmissions" pool-name="pgadmissions-mysql" enabled="true" use-java-context="true" use-ccm="true">
                <connection-url>jdbc:mysql://localhost/pgadmissions?zeroDateTimeBehavior=convertToNull</connection-url>
				<driver>mysql</driver>
				<pool>
					<min-pool-size>5</min-pool-size>
					<max-pool-size>20</max-pool-size>
				</pool>
				<security>
					<user-name>pgadmissions</user-name>
					<password>MYSQL_PASSWORD</password>
				</security>
				<validation>
					<validate-on-match>false</validate-on-match>
					<background-validation>false</background-validation>
				</validation>
				<statement>
					<share-prepared-statements>false</share-prepared-statements>
				</statement>
			</datasource>
			<drivers>
				<driver name="mysql" module="com.mysql.jdbc">
					<driver-class>com.mysql.jdbc.Driver</driver-class>
					<xa-datasource-class>com.mysql.jdbc.jdbc2.optional.MysqlXADataSource</xa-datasource-class>
				</driver>
			</drivers>
		</datasources>
	</subsystem>
