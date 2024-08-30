insert into users.role(name)
values ('ROLE_STAFF');

insert into users.permission(name)
values ('MANAGER_ROLE'),
       ('MANAGER_PERMISSION'),
       ('MANAGER_USER'),
       ('ASSIGN_PERMISSION');

insert into users.user(email, password, full_name, phone_number, birth, gender, avatar, id_google_account,
                       id_facebook_account)
values ('longjava2024@gmail.com', '$2a$10$5Knqj77YGSE6NrhpoECF5.ww5iBs.9RqG/NKGFJkPWFlK7wnusxLi',
        'Nguyen Hai Long', '0987654321', '2002-11-23', 'OTHER', NULL, NULL, NULL);

INSERT INTO users.user_roles(id_user, id_role)
values ((SELECT id FROM users.user ur WHERE ur.email = 'longjava2024@gmail.com'),
        (SELECT id FROM users.role WHERE name = 'ROLE_STAFF'));

INSERT INTO users.role_permission(id_role, id_permission)
VALUES ((SELECT id FROM users.role WHERE name = 'ROLE_STAFF'),
        (SELECT id FROM users.permission WHERE name = 'MANAGER_ROLE')),
       ((SELECT id FROM users.role WHERE name = 'ROLE_STAFF'),
        (SELECT id FROM users.permission WHERE name = 'ASSIGN_PERMISSION'));