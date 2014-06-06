EmailProvider
=============

Installation
--------------
Please install the following tools:
  * [Maven] - awesome tool to manage a project's build
  * [Tomcat]: - open source software implementation of the Java Servlet and JavaServer Pages technologies

Build
--------------
  - Clone this package
  - This project uses two email service providers: [Mailgun] and [Mandrill]. Both of then require register and obtain api keys. Please read the doc of two services to get a api key for both of them. Once you have the api keys, please refer to file */EmailProvider/src/main/resources/services.properties*, and put api keys there.
  - In the folder */EmailProvider*, run: 
```sh
  mvn package
```
  - After you see 'BUILD SUCCESS', a war file is generated as: */EmailProvider/target/email-1.0.0-BUILD-SNAPSHOT.war*

Deployment
--------------
  - After the tomcat is installed, please go to the folder. e.g. */apache-tomcat-7.0.54*
  - Copy the generated email-1.0.0-BUILD-SNAPSHOT.war to */apache-tomcat-7.0.54/webapps*
  - In order to achieve the requirement that the uri is *http://localhost:8080/email*, edit file:
*/apache-tomcat-7.0.54/conf/server.xml*: Add the following lines under <Host> section
```sh
           <Context path="" docBase="email-1.0.0-BUILD-SNAPSHOT">
           <!-- Default set of monitored resources -->
             <WatchedResource>WEB-INF/web.xml</WatchedResource>
           </Context>
           <Context path="ROOT" docBase="ROOT">
           <!-- Default set of monitored resources -->
             <WatchedResource>WEB-INF/web.xml</WatchedResource>
           </Context>
```

Note: the docBase of the first Context should be the same as the generated war file name (without .war)

Start Web Service
--------------
```sh
sh /apache-tomcat-7.0.54/bin/startup.sh
```

[Maven]:http://maven.apache.org/download.cgi
[Tomcat]:http://tomcat.apache.org/download-70.cgi
