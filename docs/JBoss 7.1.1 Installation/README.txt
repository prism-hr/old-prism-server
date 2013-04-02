 _____  ____                              
/\___ \/\  _`\                            
\/__/\ \ \ \L\ \    ___     ____    ____  
   _\ \ \ \  _ <'  / __`\  /',__\  /',__\ 
  /\ \_\ \ \ \L\ \/\ \L\ \/\__, `\/\__, `\
  \ \____/\ \____/\ \____/\/\____/\/\____/
   \/___/  \/___/  \/___/  \/___/  \/___/ 
                                          
   Installation Guide

========================================================================
                          Setting up the users
========================================================================

Log in to the Linux server and switch to the root account by entering "su" 
followed by the password.

We want to have a seperate user and group for running the JBoss AS. 
In order to create the group add the following command into the terminal:

# groupadd jboss

To create the jboss user and set his default home directory enter 
fhe following command into the terminal:

# useradd jboss –g jboss –d /usr/local/jboss

Edit the /usr/local/jboss/.bashrc file with your favourite edtior to 
include the following environment variable:

    export JAVA_HOME=/usr/lib/jvm/java-1.6.0

========================================================================
                          Installing JBoss AS7
========================================================================

Install JBoss AS7 into /usr/local/jboss

# cd /usr/local/jboss/
# wget http://download.jboss.org/jbossas/7.1/jboss-as-7.1.1.Final/jboss-as-7.1.1.Final.zip
# unzip jboss-as-7.1.1.Final.zip
# ln –s /usr/local/jboss/jboss-as-7.1.1.Final /usr/local/jboss/current
# chown -fR jboss.jboss /usr/local/jboss/

After you have successfully installed JBoss you want to add a management user which is 
allowed to access the management console.

# su jboss
# cd /usr/local/jboss/current/bin
# ./add-user.sh

You should see the following message on the console after executing the command:

    What type of user do you wish to add?
        a) Management User (mgmt-users.properties)
        b) Application User (application-users.properties)
    (a): a

We select "a", next you should see the following message:

    Enter the details of the new user to add.
        Realm (ManagementRealm) :
        Username : prism
        Password :
        Re-enter Password :

Hit enter for Realm to use default, then provide a username and password

The last step is to make sure that JBoss AS7 is started when the Linux machine 
is rebooted. Become the "root" user again and copy the "jboss" file 
provided in this directory to the /etc directory

# su
# cp jboss /etc/init.d/
# chmod 755 /etc/init.d/jboss
# chkconfig --add jboss
# chkconfig jboss on

Test whether JBoss AS7 successfully starts by entering the following command:

# service jboss start

==============================================================================================
    Add the Pgadmissions KeyStore to JBoss for authenticate with the Portico WebService
==============================================================================================

Copy the "pgadmissions.jks" provided in this folder to "/usr/local/jboss/"

# cp pgadmissions.jks /usr/local/jboss/

Edit the JBOSS_HOME/bin/standalone.conf to include the following lines:

    #
    # Specify options to pass to the Java VM.
    #
    if [ "x$JAVA_OPTS" = "x" ]; then
       JAVA_OPTS="-Xms64m -Xmx512m -XX:MaxPermSize=256m -Djava.net.preferIPv4Stack=true -Dorg.jboss.resolver.warning=true -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000"
       JAVA_OPTS="$JAVA_OPTS -Djboss.modules.system.pkgs=$JBOSS_MODULES_SYSTEM_PKGS -Djava.awt.headless=true"
       JAVA_OPTS="$JAVA_OPTS -Djboss.server.default.config=standalone.xml"
       JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.keyStore=/usr/local/jboss/pgadmissions.jks -Djavax.net.ssl.keyStorePassword=pgadmissions"
    else
       echo "JAVA_OPTS already set in environment; overriding default settings with values: $JAVA_OPTS"
    fi

========================================================================
            Install the MySQL Driver & Connection in JBoss
========================================================================

Install the MySQL Java connector as a JBoss dependency by unzipping the "jboss-7.1.1-mysql-modules.zip" provided in this directory to your $JBOSS_HOME

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
                    <netty-connector name="netty" socket-binding="messaging"/>
                    <netty-connector name="netty-throughput" socket-binding="messaging-throughput">
                        <param key="batch-delay" value="50"/>
                    </netty-connector>
                    <in-vm-connector name="in-vm" server-id="0"/>
                </connectors>

                <acceptors>
                    <netty-acceptor name="netty" socket-binding="messaging"/>
                    <netty-acceptor name="netty-throughput" socket-binding="messaging-throughput">
                        <param key="batch-delay" value="50"/>
                        <param key="direct-deliver" value="false"/>
                    </netty-acceptor>
                    <in-vm-acceptor name="in-vm" server-id="0"/>
                </acceptors>

                <security-settings>
                    <security-setting match="#">
                        <permission type="send" roles="guest"/>
                        <permission type="consume" roles="guest"/>
                        <permission type="createNonDurableQueue" roles="guest"/>
                        <permission type="deleteNonDurableQueue" roles="guest"/>
                    </security-setting>
                </security-settings>

                <address-settings>
                    <address-setting match="#">
                        <dead-letter-address>jms.queue.DLQ</dead-letter-address>
                        <expiry-address>jms.queue.ExpiryQueue</expiry-address>
                        <redelivery-delay>0</redelivery-delay>
                        <max-size-bytes>10485760</max-size-bytes>
                        <address-full-policy>BLOCK</address-full-policy>
                        <message-counter-history-day-limit>10</message-counter-history-day-limit>
                    </address-setting>
                    <address-setting match="jms.queue.DLQ">
                        <redelivery-delay>0</redelivery-delay>
                        <max-size-bytes>10485760</max-size-bytes>
                        <address-full-policy>DROP</address-full-policy>
                    </address-setting>
                    <address-setting match="jms.queue.pgadmissions-portico">
                        <dead-letter-address>jms.queue.DLQ</dead-letter-address>
                        <redelivery-delay>15000</redelivery-delay>
                        <max-delivery-attempts>4</max-delivery-attempts>
                    </address-setting>
                </address-settings>

                <jms-connection-factories>
                    <connection-factory name="InVmConnectionFactory">
                        <connectors>
                            <connector-ref connector-name="in-vm"/>
                        </connectors>
                        <entries>
                            <entry name="java:/ConnectionFactory"/>
                        </entries>
                    </connection-factory>
                    <connection-factory name="RemoteConnectionFactory">
                        <connectors>
                            <connector-ref connector-name="netty"/>
                        </connectors>
                        <entries>
                            <entry name="RemoteConnectionFactory"/>
                            <entry name="java:jboss/exported/jms/RemoteConnectionFactory"/>
                        </entries>
                    </connection-factory>
                    <pooled-connection-factory name="hornetq-ra">
                        <transaction mode="xa"/>
                        <connectors>
                            <connector-ref connector-name="in-vm"/>
                        </connectors>
                        <entries>
                            <entry name="java:/JmsXA"/>
                        </entries>
                    </pooled-connection-factory>
                </jms-connection-factories>

                <jms-destinations>
                    <jms-queue name="pgadmissions-portico">
                        <entry name="pgadmissions/portico"/>
                        <entry name="java:jboss/exported/jms/pgadmissions/portico"/>
                    </jms-queue>
                    <jms-queue name="DLQ">
                        <entry name="queue/DLQ"/>
                        <entry name="java:jboss/exported/jms/queue/DLQ"/>
                    </jms-queue>
                </jms-destinations>
            </hornetq-server>
        </subsystem>

Add the following two lines to the <socket-binding-group> tag:

    <socket-binding name="messaging" port="5445" />
    <socket-binding name="messaging-throughput" port="5455" />

========================================================================
        Using Apache as a REVERSE PROXY for JBoss
========================================================================

Install the Apache HTTPD if it is not already installed.

# yum install httpd

Make sure that the httpd is being started when the Linux machine starts:

# chkconfig httpd on
# service httpd start

Configure the reverse proxy settings by changing the "/etc/httpd/conf/httpd.conf" file 
accordingly:

    <IfModule mod_proxy.c>
    ProxyPass / http://localhost:8080/
    ProxyPassReverse / http://localhost:8080/

    #<Proxy *>
    #    Order deny,allow
    #    Deny from all
    #    Allow from .example.com
    #</Proxy>

At the end of the file add:

    RewriteEngine  on
    RewriteRule    ^/$  /pgadmissions [R]
    RewriteRule    ^$  /pgadmissions [R]

If you are running SELinux:

    setsebool -P httpd_can_network_connect 1

========================================================================
        Accessing the JBoss Management Console
========================================================================

Create a SSH tunnel and forward the JBoss Management Console port to your 
local machine by issuing the following command (make sure you are in zred):    

    ssh -L 9990:localhost:9990 prism@prism.ucl.ac.uk

Then point your browser to:
    
    http://localhost:9990/console/

Username: prism / Password: pg@m1ss1on

