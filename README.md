//====================================== How To Install And Run The Online System For Scientific Paper Submission ===================================================//

Prepear:
- IDE (IntelliJ IDEA, Eclipse, ...).
- MySQL Workbench. 
- Install Java JDK version 17 or higher. 

Start setup:
1. Download the project from github: https://github.com/lenhattruong111000/thesis-web-submission-system
2. Extract and add it in your IDE workplace
3. Use MySQL to excecute sql_submission.sql file to create the database 
4. Open file application.properties with direction: /web_submission_system-1/src/main/resources/application.properties
5. In application.properties file, change username and password to access MySQL database, also change the username and password to use an email smtp
6. Depend on your IDE, install all tools support to running the Spring Boot project.
7. To Run the project, please go to direction: /web_submission_system-1/src/main/java/org/hcmiu/submission_system/spring/WebSubmissionSystem1Application.java
8. Run file WebSubmissionSystem1Application.java as Spring Boot App
9. Now, project are running.
