insert into dblearnstar.roles (name) values ('ADMINISTRATOR'),('INSTRUCTOR');
insert into dblearnstar.person (user_name, first_name, last_name) values ('admin', 'Administrator', 'Administrator');
insert into dblearnstar.person_role (person_id, role_id) values ((select person_id from dblearnstar.person where user_name='admin'), (select role_id from dblearnstar.roles where name='ADMINISTRATOR'));
