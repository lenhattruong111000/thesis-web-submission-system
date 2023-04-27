//===== How To Install And Run The Online System For Scientific Paper Submission =====//

Prepear:
- IDE (IntelliJ IDEA, Eclipse, ...).
- MySQL Workbench. 
- Install Java JDK version 17 or higher. 
- Ensure your IDE is having all tools to support Spring Boot Project Running.

Start setup:

1. Download the project from github: https://github.com/lenhattruong111000/thesis-web-submission-system .
2. Extract and add it in your IDE workplace.
3. Use MySQL to excecute sql_submission.sql file to create the database.
4. Open file application.properties with direction: /web_submission_system-1/src/main/resources/application.properties.
5. In application.properties file, change username and password to access MySQL database, also change the username and password to use an email smtp.
6. Depend on your IDE, install all tools support to running the Spring Boot project.
7. To Run the project, please go to direction: /web_submission_system-1/src/main/java/org/hcmiu/submission_system/spring/WebSubmissionSystem1Application.java .
8. Run file WebSubmissionSystem1Application.java as Spring Boot App.
9. Now, project are running.

-- ------------------------------------------------------------------------------
Guide details fro Eclipse IDE(having all tools to support Spring Boot Project Running):
Tools supoort for Eclipse to run the Spring Boot project(you can download in Eclipse Marketplace). 

![image](https://user-images.githubusercontent.com/59330536/234747672-1bb80178-d869-4ec2-a3b5-5653c0cc9cda.png)
![image](https://user-images.githubusercontent.com/59330536/234747740-046af184-7206-467d-b582-ff6557da6e8a.png)
![image](https://user-images.githubusercontent.com/59330536/234747898-a65141ce-0d4a-419e-ba61-818429e412b8.png)
![image](https://user-images.githubusercontent.com/59330536/234748047-c8f2f1c2-5a8a-4062-9b7d-8ef2b1c2dac4.png)
![image](https://user-images.githubusercontent.com/59330536/234748103-122d8363-0bd3-4448-be46-35d26dadbb7e.png)

1. download project and extract.
![image](https://user-images.githubusercontent.com/59330536/234747473-087ddbb8-ce0e-4688-90ad-98ee4e0fc167.png)

2. Open Eclipse IDE, and select the workplace that contains the project(Select the directory of project).
![image](https://user-images.githubusercontent.com/59330536/234743640-0d2e067a-8432-4837-9551-b8e889f313fb.png)

3. Press 'Launch' button to access the new workplace.
4. Select File -> Open Project from File System... .
![image](https://user-images.githubusercontent.com/59330536/234743770-7029f165-77a8-4b72-8b23-04849232419e.png)

5. input the direction of project to 'Import source' then press 'Finish' button.
![image](https://user-images.githubusercontent.com/59330536/234744012-40200cff-3270-490a-9b8b-95cea6ea71fd.png)

6. Use MySQL Workbench to excecute sql_submission.sql file to create the database. 
![image](https://user-images.githubusercontent.com/59330536/234744506-bf762d85-5747-4906-82b5-14fb24d63931.png)
![image](https://user-images.githubusercontent.com/59330536/234744712-29890d3d-783f-43d1-b68f-a8d07b4453f8.png)
![image](https://user-images.githubusercontent.com/59330536/234744809-09a6c2a3-2bc9-4f5f-a77d-a672ec9944f1.png)

7. Open file application.properties with direction: /web_submission_system-1/src/main/resources/application.properties .
8. In application.properties file, change username and password to access MySQL database, also change the username and password to use an email smtp.
![image](https://user-images.githubusercontent.com/59330536/234746001-84e5dab9-7f15-43d2-8e09-61ee451b9720.png)

9. To Run the project, please go to direction: /web_submission_system-1/src/main/java/org/hcmiu/submission_system/spring/WebSubmissionSystem1Application.java .
10. Run file WebSubmissionSystem1Application.java as Spring Boot App.
![image](https://user-images.githubusercontent.com/59330536/234746201-d60c5a89-d51a-4052-8db2-c05111952e27.png)

11. Now, project are running.
![image](https://user-images.githubusercontent.com/59330536/234746423-afb8692d-16bb-4b20-805a-b8500cd6df52.png)

-- ------------------------------------------------------------------------------
The Web Submission System is designed by Le Nhat Truong - ITITIU18257 
From VIETNAM NATIONAL UNIVERSITY OF HOCHIMINH CITY - THE INTERNATIONAL UNIVERSITY

