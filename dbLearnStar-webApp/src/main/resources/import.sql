INSERT INTO dblearnstar.task_type (codetype, title) values ('SQL','SQL'),('DDL','DDL'),('TEXT','TEXT'),('UPLOAD','UPLOAD');
INSERT INTO dblearnstar.test_type (title) values ('Теориски'),('Практичен'),('Вежби'),('Експериментирање');
INSERT INTO dblearnstar.roles (name) values ('ADMINISTRATOR'),('INSTRUCTOR');
INSERT INTO dblearnstar.person (user_name, first_name, last_name) values ('admin', 'Administrator', 'Administrator');
DELETE FROM dblearnstar.person_role WHERE person_id=(select person_id from dblearnstar.person where user_name='admin') and role_id=(select role_id from dblearnstar.roles where name='ADMINISTRATOR');
INSERT INTO dblearnstar.person_role (person_id, role_id) values ((select person_id from dblearnstar.person where user_name='admin'), (select role_id from dblearnstar.roles where name='ADMINISTRATOR'));
DELETE FROM dblearnstar.student WHERE person_id=(select person_id from dblearnstar.person where user_name='admin');
INSERT INTO dblearnstar.student (person_id) values ((select person_id from dblearnstar.person where user_name='admin'));
