--liquibase formatted sql

--changeset krupenko:1
-- This is the insertion of the 1st user: petr/petr
insert into users
    (name, date_of_birth, password)
values ('petr', '1988-08-08', '$2a$12$NfotrZBvHm5MCDVvasqjZegD/l4wcmWTMFupTOq4vRUNXJ.tlAATC');

insert into accounts
    (user_id, balance)
values (currval('users_id_seq'), 100);

insert into phone_data
    (user_id, phone)
    values (currval('users_id_seq'), '79207865432');

insert into email_data
    (user_id, email)
values (currval('users_id_seq'), 'petr@gmail.com');

--changeset krupenko:2
-- This is the insertion of the 2nd user: ivan/ivan
insert into users
    (name, date_of_birth, password)
values ('ivan', '1977-07-07', '$2a$12$t8Dg0W/QRFx6B/5Od65eM.lXfLtR66o.QM8ohPwzS6EKE.pFACnyG');

insert into accounts
    (user_id, balance)
values (currval('users_id_seq'), 432);

insert into phone_data
    (user_id, phone)
values (currval('users_id_seq'), '79876543210');

insert into email_data
    (user_id, email)
values (currval('users_id_seq'), 'ivan@gmail.com');

--changeset krupenko:3
-- This is the insertion of the 3rd user: nika/nika
insert into users
(name, date_of_birth, password)
values ('nika', '2000-01-01', '$2a$12$hiwfItdm24XAr0em4uZ.peDLVcROCPFHL5tBNiwkiTctzL5nCB.pW');

insert into accounts
(user_id, balance)
values (currval('users_id_seq'), 1010);

insert into phone_data
(user_id, phone)
values (currval('users_id_seq'), '375291010101');

insert into email_data
(user_id, email)
values (currval('users_id_seq'), 'nika@gmail.com');