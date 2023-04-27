drop database if exists `submissionSystem`;
CREATE DATABASE  IF NOT EXISTS `submissionSystem`;
use `submissionSystem`;

-- Create table
create table `App_User`
(
  USER_ID BIGINT auto_increment ,
  Full_Name varchar(40),
  ENABLED           BIT,
  USER_NAME         VARCHAR(36) ,
  ENCRYTED_PASSWORD VARCHAR(128) ,
  USER_EMAIL varchar(50) ,
  USER_PHONE varchar(11) ,
  Verification_Code varchar(64) ,
  constraint APP_USER_PK primary key (USER_ID),
  constraint APP_USER_UK unique (USER_NAME)
) ;


-- Create table
create table `App_Role`
(
  ROLE_ID   BIGINT ,
  ROLE_NAME VARCHAR(30) ,
  constraint APP_ROLE_PK primary key (ROLE_ID),
  constraint APP_ROLE_UK unique (ROLE_NAME)
) ;

-- Create table
create table `USER_ROLE`
(
  ID      BIGINT auto_increment ,
  USER_ID BIGINT ,
  ROLE_ID BIGINT ,
  constraint USER_ROLE_PK primary key (ID),
  constraint USER_ROLE_UK unique (USER_ID, ROLE_ID),
  constraint USER_ROLE_FK1 foreign key (USER_ID) references `App_User` (USER_ID),
  constraint USER_ROLE_FK2 foreign key (ROLE_ID) references `App_Role` (ROLE_ID)
  
);

drop table if exists `author`;
create table author(
id bigint auto_increment,
user_id bigint,
primary key(id),
constraint author_Infor_FK foreign key (user_id) references `App_User` (USER_ID)
);

drop table if exists `editor`;
create table editor(
id bigint auto_increment,
user_id bigint,
primary key(id),
constraint Editor_Infor_FK foreign key (user_id) references `App_User` (USER_ID)
);

drop table if exists `reviewer`;
create table reviewer(
id bigint auto_increment,
user_id bigint,
master_field varchar(255),
primary key(id),
fulltext(master_field),
constraint Reviewer_Infor_FK foreign key (user_id) references `App_User` (USER_ID)
) ENGINE=InnoDB;

drop table if exists `files`;
create table files (
id bigint auto_increment, 
type varchar(30),
file_name varchar(255), 
content longblob, 
size varchar(255), 
sfile_name varchar(255), 
scontent longblob, 
ssize varchar(255),
primary key (id)
);
drop table if exists `Submission_Infor`;
create table Submission_Infor(
s_id bigint auto_increment ,
s_authorid bigint,
s_title text(65535)  ,
s_major varchar(50) ,
s_authorname varchar(30) ,
s_workplace varchar(100) ,
s_country varchar(50),
s_abstract text(65535) ,
s_keyword varchar(255),
s_comment varchar(255),
s_state varchar(30) ,
fid bigint,
primary key(s_id),
constraint SUBMISSION_Infor_FK foreign key (s_authorid) references `App_User` (USER_ID),
constraint SUBMISSION_FILE_FK foreign key (fid) references `files` (id)

);

drop table if exists `coauthor`;
create table coauthor (
id bigint auto_increment,
s_id bigint,
co_fullname varchar(30),
co_email varchar(50),
co_country varchar(50),
co_organization varchar(100),
co_webpage varchar(255),
co_correspondingauthor bit,
primary key(id),
constraint  SUBMISSION_COAUTHOR_FK foreign key (s_id) references `Submission_Infor` (s_id)
);

--------------------------------------
insert into App_User (USER_ID, Full_Name, ENABLED, USER_NAME, ENCRYTED_PASSWORD, USER_EMAIL, USER_PHONE)
values (1,'Mary', 1, 'dbadmin1' , '$2a$10$PrI5Gk9L.tSZiW9FXhTS8O8Mz9E97k2FZbFvGFFaSsiTUIl.TCrFu', 'truonglnfx21573@funix.edu.vn', '0333393333');

insert into App_User (USER_ID, Full_Name, ENABLED, USER_NAME, ENCRYTED_PASSWORD, USER_EMAIL, USER_PHONE)
values (2,'John', 1,'dbuser1', '$2a$10$PrI5Gk9L.tSZiW9FXhTS8O8Mz9E97k2FZbFvGFFaSsiTUIl.TCrFu', 'lenhattruong2015@gmail.com','080354325');

insert into App_User (USER_ID, Full_Name, ENABLED, USER_NAME, ENCRYTED_PASSWORD, USER_EMAIL, USER_PHONE)
values (3,'Moon', 1,'dbreview1' ,'$2a$10$PrI5Gk9L.tSZiW9FXhTS8O8Mz9E97k2FZbFvGFFaSsiTUIl.TCrFu', 'ititiu18257@student.hcmiu.edu.vn','0943423476');

insert into App_User (USER_ID, Full_Name, ENABLED, USER_NAME, ENCRYTED_PASSWORD, USER_EMAIL, USER_PHONE)
values (4,'Sun', 1,'dbreview2' ,'$2a$10$PrI5Gk9L.tSZiW9FXhTS8O8Mz9E97k2FZbFvGFFaSsiTUIl.TCrFu', 'jobandjob336@gmail.com','0908035432');

insert into App_User (USER_ID, Full_Name, ENABLED, USER_NAME, ENCRYTED_PASSWORD, USER_EMAIL, USER_PHONE)
values (5,'Anh', 1,'dbreview3' ,'$2a$10$PrI5Gk9L.tSZiW9FXhTS8O8Mz9E97k2FZbFvGFFaSsiTUIl.TCrFu', 'acbsystem30@gmail.com','0933423423');

insert into App_User (USER_ID, Full_Name, ENABLED, USER_NAME, ENCRYTED_PASSWORD, USER_EMAIL, USER_PHONE)
values (6,'Minh', 1,'dbreview4' ,'$2a$10$PrI5Gk9L.tSZiW9FXhTS8O8Mz9E97k2FZbFvGFFaSsiTUIl.TCrFu', 'minh@gmail.com','0933476573');

insert into App_User (USER_ID, Full_Name, ENABLED, USER_NAME, ENCRYTED_PASSWORD, USER_EMAIL, USER_PHONE)
values (7,'Lan', 1,'dbreview5' ,'$2a$10$PrI5Gk9L.tSZiW9FXhTS8O8Mz9E97k2FZbFvGFFaSsiTUIl.TCrFu', 'lan@gmail.com','0937883423');

insert into App_User (USER_ID, Full_Name, ENABLED, USER_NAME, ENCRYTED_PASSWORD, USER_EMAIL, USER_PHONE)
values (8,'Trong', 1,'dbreview6' ,'$2a$10$PrI5Gk9L.tSZiW9FXhTS8O8Mz9E97k2FZbFvGFFaSsiTUIl.TCrFu', 'trong@gmail.com','0963679023');

insert into App_User (USER_ID, Full_Name, ENABLED, USER_NAME, ENCRYTED_PASSWORD, USER_EMAIL, USER_PHONE)
values (9,'Mai', 1,'dbreview7' ,'$2a$10$PrI5Gk9L.tSZiW9FXhTS8O8Mz9E97k2FZbFvGFFaSsiTUIl.TCrFu', 'mai@gmail.com','0955468443');

insert into App_User (USER_ID, Full_Name, ENABLED, USER_NAME, ENCRYTED_PASSWORD, USER_EMAIL, USER_PHONE)
values (10,'Khoa', 1,'dbreview8' ,'$2a$10$PrI5Gk9L.tSZiW9FXhTS8O8Mz9E97k2FZbFvGFFaSsiTUIl.TCrFu', 'khoa@gmail.com','0988423423');

-- -

insert into `App_Role` (ROLE_ID, ROLE_NAME)
values (1, 'ROLE_EDITOR');

insert into `App_Role` (ROLE_ID, ROLE_NAME)
values (2, 'ROLE_AUTHOR');
INSERT INTO `submissionsystem`.`app_role` (`ROLE_ID`, `ROLE_NAME`) 
VALUES ('3', 'ROLE_REVIEWER');

--

insert into user_role (ID, USER_ID, ROLE_ID)
values (1, 1, 1);

insert into user_role (ID, USER_ID, ROLE_ID)
values (2, 2, 2);

insert into user_role (ID, USER_ID, ROLE_ID)
values (3, 3, 3);
insert into user_role (ID, USER_ID, ROLE_ID)
values (4, 4, 3);
insert into user_role (ID, USER_ID, ROLE_ID)
values (5, 5, 3);

insert into user_role (ID, USER_ID, ROLE_ID)
values (6, 6, 3);
insert into user_role (ID, USER_ID, ROLE_ID)
values (7, 7, 3);
insert into user_role (ID, USER_ID, ROLE_ID)
values (8, 8, 3);
insert into user_role (ID, USER_ID, ROLE_ID)
values (9, 9, 3);
insert into user_role (ID, USER_ID, ROLE_ID)
values (10, 10, 3);

--
insert into editor values(1,1);
insert into author values(1,2);
insert into reviewer values(1,3,'computer science, network science, data science');
insert into reviewer values(2,4, 'computer science, data science');
insert into reviewer values(3,5, 'computer science, security science, math, social');

insert into reviewer values(4,6, 'security science, network science, social');
insert into reviewer values(5,7, 'biology science, social');
insert into reviewer values(6,8, 'security science, math, physics');
insert into reviewer values(7,9, 'network science, math, social');
insert into reviewer values(8,10, 'security science, data science , social');

-- --------------------------------------
drop table if exists `ReceiveManuscript`;
create table ReceiveManuscript(
id bigint auto_increment,
s_id bigint unique,
editor_id bigint,
primary key(id),
constraint Editor_Id_FK foreign key (editor_id) references `editor` (user_id),
constraint Submission_Id_FK foreign key (s_id) references `Submission_Infor` (s_id),
constraint SUBMISSION_UK unique (s_id)
);
-- --------------------------------------
drop table if exists `Manuscript_Review`;
create table `Manuscript_Review`(
	id bigint auto_increment,
    reviewer_id bigint,
    s_id bigint,
    s_deadlinedate date,
    s_deadlinetime time,
    review_date date,
    review_time time,
    is_late bit,
    editor_comment text(65535),
    reviewer_state varchar(30),
    reviewer_comment text(65535),
    primary key(id),
    constraint REVIEW_SUBMISSION_FK foreign key (s_id) references `Submission_Infor` (s_id),
    constraint REVIEWER_ACCOUNT_FK foreign key (reviewer_id) references `App_User` (USER_ID)
    
);
SET GLOBAL max_allowed_packet=1073741824;
show variables like 'max_allowed_packet';





















-- ----------------------------------
-- Used by Spring Remember Me API.  
CREATE TABLE Persistent_Logins (

    username varchar(64) not null,
    series varchar(64) not null,
    token varchar(64) not null,
    last_used timestamp not null,
    PRIMARY KEY (series)
    
);

