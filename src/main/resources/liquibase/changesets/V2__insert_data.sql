insert into users (name, username, password)
values ('admin', 'admin', '$2a$10$gW8OCDNYOrmAn7.NCktjbe3fYGfM816OeVMtGKzmHe1KAJdE5j2k6'),
       ('test2test', 'test2test', '$2a$10$UKvzQOxwwaJJT5IbT.0eQ.aFFNnnFrvD8I44I8pIN0Hh3gnHPawse'),
       ('test4test', 'test4test', '$2a$10$6EsjC/I9/nDld9bAYyvoX.jBeSTL5ejDCGLnjpTlbNN0Ct7a6gLda');

insert into tasks (title, description, status, expiration_date)
values ('Buy cheese', null, 'TODO', '2025-10-29 12:00:00'),
       ('Do homework', 'Math, Physics, Literature', 'IN_PROGRESS', '2025-10-31 00:00:00'),
       ('Clean rooms', null, 'DONE', null),
       ('Call Mike', 'Ask about meeting', 'TODO', '2025-10-01 00:00:00');

insert into users_tasks (task_id, user_id)
values (1, 2),
       (2, 2),
       (3, 2),
       (4, 1);

insert into users_roles (user_id, role)
values (1, 'ROLE_ADMIN'),
       (1, 'ROLE_USER'),
       (2, 'ROLE_USER');