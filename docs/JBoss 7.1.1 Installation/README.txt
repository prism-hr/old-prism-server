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

Install the MySQL Java connector as a JBoss dependency by unzipping the jboss-7.1.1-mysql-modules.zip into your $JBOSS_HOME

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

========================================================================
        Encrypt your MySQL Password in the JBoss configuration file
========================================================================

If you don't want to have your database password in plaintext embedded in the JBoss configuration XML run the following 
command to encrypt the password.

# cd $JBOSS_HOME
# java -cp "modules/org/jboss/logging/main/jboss-logging-3.1.0.GA.jar;modules/org/picketbox/main/picketbox-4.0.7.final.jar" org.picketbox.datasource.security.SecureIdentityLoginModule pgadmissions

Ammend your JBoss datsource configuration file like this:
    
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
                        <security-domain>Pgadmissions</security-domain>
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
        ...
        <subsystem xmlns="urn:jboss:domain:security:1.1">
            <security-domains>
                <security-domain name="Pgadmissions">
                    <authentication>
                        <login-module code="SecureIdentity" flag="required">
                            <module-option name="username" value="pgadmissions" />
                            <module-option name="password" value="-14efcab0e8d3cd3551c3e552326fd0d7" />
                        </login-module>
                    </authentication>
                </security-domain>
            </security-domains>
            ...
        </subsystem>

========================================================================
        Enable JMS queues on JBoss
========================================================================

Add the following entry to your <extension> tag:

    <extension module="org.jboss.as.messaging" />

Append the following subsystem to your JBoss configuration file:

    <subsystem xmlns="urn:jboss:domain:messaging:1.1">
        <hornetq-server>
            <persistence-enabled>true</persistence-enabled>
            <journal-file-size>102400</journal-file-size>
            <journal-min-files>2</journal-min-files>

            <connectors>
                <netty-connector name="netty" socket-binding="messaging" />
                <netty-connector name="netty-throughput"
                    socket-binding="messaging-throughput">
                    <param key="batch-delay" value="50" />
                </netty-connector>
                <in-vm-connector name="in-vm" server-id="0" />
            </connectors>

            <acceptors>
                <netty-acceptor name="netty" socket-binding="messaging" />
                <netty-acceptor name="netty-throughput"
                    socket-binding="messaging-throughput">
                    <param key="batch-delay" value="50" />
                    <param key="direct-deliver" value="false" />
                </netty-acceptor>
                <in-vm-acceptor name="in-vm" server-id="0" />
            </acceptors>

            <security-settings>
                <security-setting match="#">
                    <permission type="send" roles="guest" />
                    <permission type="consume" roles="guest" />
                    <permission type="createNonDurableQueue" roles="guest" />
                    <permission type="deleteNonDurableQueue" roles="guest" />
                </security-setting>
            </security-settings>

            <address-settings>
                <address-setting match="#">
                    <dead-letter-address>jms.queue.DLQ</dead-letter-address>
                    <expiry-address>jms.queue.ExpiryQueue</expiry-address>
                    <redelivery-delay>0</redelivery-delay>
                    <max-size-bytes>10485760</max-size-bytes>
                    <address-full-policy>BLOCK</address-full-policy>
                    <message-counter-history-day-limit>10
                    </message-counter-history-day-limit>
                </address-setting>

                <!-- Redelivery policies for JMS messages -->
                <address-setting match="jms.queue.pgadmissions-portico">
                    <!-- 15min -->
                    <redelivery-delay>900000</redelivery-delay>
                    <dead-letter-address>jms.queue.DLQ</dead-letter-address>
                    <max-delivery-attempts>4</max-delivery-attempts>
                </address-setting>
                
                <address-setting match="jms.queue.pgadmissions-mail">
                    <!-- 15min -->
                    <redelivery-delay>900000</redelivery-delay>
                    <dead-letter-address>jms.queue.DLQ</dead-letter-address>
                    <max-delivery-attempts>4</max-delivery-attempts>
                </address-setting>
            </address-settings>

            <jms-connection-factories>
                <connection-factory name="InVmConnectionFactory">
                    <connectors>
                        <connector-ref connector-name="in-vm" />
                    </connectors>
                    <entries>
                        <entry name="java:/ConnectionFactory" />
                    </entries>
                </connection-factory>
                <connection-factory name="RemoteConnectionFactory">
                    <connectors>
                        <connector-ref connector-name="netty" />
                    </connectors>
                    <entries>
                        <entry name="RemoteConnectionFactory" />
                        <entry name="java:jboss/exported/jms/RemoteConnectionFactory" />
                    </entries>
                </connection-factory>
                <pooled-connection-factory name="hornetq-ra">
                    <transaction mode="xa" />
                    <connectors>
                        <connector-ref connector-name="in-vm" />
                    </connectors>
                    <entries>
                        <entry name="java:/JmsXA" />
                    </entries>
                </pooled-connection-factory>
            </jms-connection-factories>

            <jms-destinations>
                <jms-queue name="pgadmissions-mail">
                    <entry name="pgadmissions/mail" />
                    <entry name="java:jboss/exported/jms/pgadmissions/mail" />
                </jms-queue>
                <jms-queue name="pgadmissions-portico">
                    <entry name="pgadmissions/portico" />
                    <entry name="java:jboss/exported/jms/pgadmissions/portico" />
                </jms-queue>
            </jms-destinations>
        </hornetq-server>
    </subsystem>

Add the following two lines to the <socket-binding-group> tag:

    <socket-binding name="messaging" port="5445" />
    <socket-binding name="messaging-throughput" port="5455" />

==============================================================================================
    Add the Pgadmissions KeyStore to JBoss for authenticate with the Portico WebService
==============================================================================================

Ammend the start script to include the following two JVM options:

    JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.keyStore=/usr/local/apache-tomcat-7.0.29_8080/pgadmissions.jks"
    JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.keyStorePassword=pgadmissions"
