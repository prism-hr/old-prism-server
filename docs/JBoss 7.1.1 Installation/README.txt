 _____  ____                              
/\___ \/\  _`\                            
\/__/\ \ \ \L\ \    ___     ____    ____  
   _\ \ \ \  _ <'  / __`\  /',__\  /',__\ 
  /\ \_\ \ \ \L\ \/\ \L\ \/\__, `\/\__, `\
  \ \____/\ \____/\ \____/\/\____/\/\____/
   \/___/  \/___/  \/___/  \/___/  \/___/ 
                                          
   Installation Guide


TODO: FINISH THIS GUIDE!

Unzip this to $JBOSS_HOME/modules to install the MySQL connector as a module

cd into JBOSS_HOME
java -cp "modules/org/jboss/logging/main/jboss-logging-3.1.0.GA.jar;modules/org/picketbox/main/picketbox-4.0.7.final.jar" org.picketbox.datasource.security.SecureIdentityLoginModule pgadmissions

JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.keyStore=/usr/local/apache-tomcat-7.0.29_8080/pgadmissions.jks"
JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.keyStorePassword=pgadmissions"
