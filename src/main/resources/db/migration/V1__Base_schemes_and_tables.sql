create schema partypal_base;
create schema partypal_event;
create schema partypal_location;
create schema partypal_tg;
create schema partypal_user;

create table partypal_base.lang_global
(
    id   bigserial
        primary key,
    code varchar(255)
        unique,
    name varchar(255)
);
insert into partypal_base.lang_global (code, name)
VALUES ('KK', 'Қазақша');
insert into partypal_base.lang_global (code, name)
VALUES ('RU', 'Русский');
insert into partypal_base.lang_global (code, name)
VALUES ('EN', 'English');

CREATE EXTENSION IF NOT EXISTS hstore;

create table partypal_event.category
(
    id        bigserial
        primary key,
    name      varchar(255),
    code      varchar(255)
        unique,
    languages hstore
);
insert into partypal_event.category (name, code, languages)
VALUES ('"Фильмы и кино"', 'Cinema', 'ru => "Фильмы и кино", en => Cinema, kz => "Фильмдер және кино"');
insert into partypal_event.category (name, code, languages)
VALUES ('"Спорт и активности"', 'Sport',
        'ru => "Спорт и активности", en => "Sport and activities", kz => "Спорт және жаттығулар"');
insert into partypal_event.category (name, code, languages)
VALUES ('"Культура и искусство"', 'Culture', 'ru => "Культура и искусство", en => "Culture and Art", kz => Мәдениет');
insert into partypal_event.category (name, code, languages)
VALUES ('"Кулинария и рестораны"', 'Restaurant',
        'ru => "Кафе и рестораны", en => Restaurants, kz => "Мейрамханалар мен дәмханалар"');
insert into partypal_event.category (name, code, languages)
VALUES ('"Фильмы и кино"', 'Education',
        'ru => "Образование и лекции", en => "Education and lectures", kz => "Білім және дәрістер"');
insert into partypal_event.category (name, code, languages)
VALUES ('"Вечеринки, встречи, сетевые мероприятия и другие события"', 'Party',
        'ru => "Вечеринки, встречи, сетевые мероприятия и другие события", en => "Parties, meetings, networking events and other events", kz => "Кештер, кездесулер, желілік іс-шаралар және басқа да іс-шаралар"');

create table partypal_location.country
(
    id   bigserial
        primary key,
    name varchar(255),
    code varchar(255)
        unique
);
insert into partypal_location.country (name, code)
VALUES ('Kazakhstan', 'KZ');


create table partypal_location.time_zone
(
    id         bigserial
        primary key,
    code       varchar(255),
    name       varchar(255),
    utc_offset varchar(255)
);
insert into partypal_location.time_zone(id, code, name, utc_offset)
VALUES (34, 'ALMT', '"Alma-Ata Time"', '"UTC +6"');
insert into partypal_location.time_zone(id, code, name, utc_offset)
VALUES (39, 'AQTT', '"Aqtobe Time"', '"UTC +5"');


create table partypal_location.city
(
    id           bigserial
        primary key,
    country_id   bigint
        references partypal_location.country
            on delete set null,
    time_zone_id bigint
        references partypal_location.time_zone
            on delete set null,
    languages    hstore,
    code         varchar
        unique,
    name         varchar
);

create table partypal_tg.state
(
    id   bigserial
        primary key,
    code varchar not null
        unique
);

insert into partypal_tg.state (code) values
('USER_CREATED'),
('EVENT_CREATED'),
('EVENT_CREATED_NAME_SELECTED'),
('EVENT_CREATED_DESCRIPTION_SELECTED'),
('EVENT_CREATED_REQUIREMENTS_SELECTED'),
('EVENT_CREATED_CATEGORY_SELECTED'),
('EVENT_CREATED_CITY_SELECTED'),
('EVENT_CREATED_LOCATION_SELECTED'),
('EVENT_UPDATE'),
('EVENT_UPDATE_CITY_SELECT'),
('EVENT_UPDATE_CATEGORY_SELECT'),
('EVENT_UPDATE_LOCATION_SELECT'),
('EVENT_UPDATE_NAME_SELECT'),
('EVENT_UPDATE_DESCRIPTION_SELECT'),
('EVENT_UPDATE_REQUIREMENTS_SELECT'),
('DEFAULT'),
('EVENT_DATE_SELECT'),
('EVENT_TIME_SELECT'),
('EVENT_PHOTO_SELECT');

create table partypal_user.gender
(
    id       bigserial
        primary key,
    name     varchar(255),
    code     varchar(255)
        unique,
    language hstore
);
insert into partypal_user.gender (name, code, language)
values ('Мужской', 'MALE', 'ru => Мужской, en => Male, kz => Ер');
insert into partypal_user.gender (name, code, language)
values ('Женский', 'FEMALE', 'ru => Женский, en => Female, kz => Әйел');
insert into partypal_user.gender (name, code, language)
values ('Неопределенный', 'UNDEFINED', 'ru => Неопределенный, en => Undefined, kz => Белгісіз');

create table partypal_user.role
(
    id       bigserial
        primary key,
    name     varchar(255),
    code     varchar(255)
        unique,
    language hstore
);
insert into partypal_user.role (name, code, language)
values ('Администратор', 'ADMIN', 'ru => Администратор, en => Admin, kz => Администратор');
insert into partypal_user.role (name, code, language)
values ('Пользователь', 'USER', 'ru => Пользователь, en => User, kz => Пользователь');

create table partypal_user.user_status
(
    id   bigserial
        primary key,
    name varchar(255),
    code varchar(255)
        unique
);
insert into partypal_user.user_status(name, code) VALUES ('Активный', 'ACTIVE');
insert into partypal_user.user_status(name, code) VALUES ('Неактивный', 'INACTIVE');
insert into partypal_user.user_status(name, code) VALUES ('Заблокированный', 'BLOCKED');

create table partypal_user.user_account
(
    id                   bigserial
        primary key,
    first_name           varchar(255) not null,
    last_name            varchar(255),
    user_status_id       bigint       not null
        references partypal_user.user_status
            on delete set null,
    date_created         date         not null,
    date_of_birth        date,
    current_city_id      bigint
                                      references partypal_location.city
                                          on delete set null,
    gender_id            bigint
                                      references partypal_user.gender
                                          on delete set null,
    last_login_date_time timestamp,
    telegram_id          bigint
        unique,
    lang                 varchar,
    telegram_username    varchar,
    current_state        bigint
                                      references partypal_tg.state
                                          on delete set null
);

create table partypal_user.user_role_link
(
    user_id bigint not null
        constraint user_role_link_user_fk
            references partypal_user.user_account,
    role_id bigint not null
        constraint user_role_link_role_fk
            references partypal_user.role,
    primary key (user_id, role_id)
);

create table partypal_event.event
(
    id           bigserial
        primary key,
    name         varchar,
    description  varchar,
    country      bigint
        references partypal_location.country
            on delete set null,
    city         bigint
        references partypal_location.city
            on delete set null,
    place        varchar,
    date         date,
    time         time,
    requirements varchar,
    created_user bigint
        references partypal_user.user_account
            on delete set null,
    category     bigint
        references partypal_event.category
            on delete set null,
    tg_id        bigint
);

alter table partypal_event.event
    add column detected_language varchar;

create table partypal_event.event_user_reference(
    user_id bigint references partypal_user.user_account(id),
    event_id bigint references partypal_event.event(id)
);
alter table partypal_event.event_user_reference add column id bigserial primary key;

create table partypal_tg.document(
    id bigserial primary key,
    tg_id varchar not null,
    tg_unique_id varchar not null,
    name varchar
);
alter table partypal_tg.document add column size bigint;
alter table partypal_event.event add column document_id bigint references partypal_tg.document (id) on delete set null;
alter table partypal_user.user_account add column actual_event_id bigint references partypal_event.event(id) on delete set null;

create table partypal_event.subscription(
    id BIGSERIAL primary key,
    code varchar unique not null,
    price int,
    days bigint
);

insert into partypal_event.subscription (code, price, days) VALUES ('WEEK_5', 940, 7);
insert into partypal_event.subscription (code, price, days) VALUES ('TWO_WEEK_5', 1540, 14);
insert into partypal_event.subscription (code, price, days) VALUES ('MONTH_5', 2410, 30);

create table partypal_event.subscription_event_reference(
    id bigserial primary key,
    event_id bigint references partypal_event.event(id) on delete cascade,
    subscription_id bigint references partypal_event.subscription(id) on delete cascade,
    promote_until date
);

alter table partypal_event.event add column subscription_ref bigint references partypal_event.subscription_event_reference(id) on delete set null;