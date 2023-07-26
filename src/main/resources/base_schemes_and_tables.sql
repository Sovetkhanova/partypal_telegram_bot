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
insert into partypal_base.lang_global (code, name) VALUES ('KK' , 'Қазақша');
insert into partypal_base.lang_global (code, name) VALUES ('RU' , 'Русский');
insert into partypal_base.lang_global (code, name) VALUES ('EN' , 'English');

create type hstore;

create table partypal_event.category
(
    id        bigserial
        primary key,
    name      varchar(255),
    code      varchar(255)
        unique,
    languages hstore
);
insert into partypal_event.category (name, code, languages) VALUES ('"Фильмы и кино"', 'Cinema' ,'ru => "Фильмы и кино", en => Cinema, kz => "Фильмдер және кино"');
insert into partypal_event.category (name, code, languages) VALUES ('"Спорт и активности"', 'Sport' ,'ru => "Спорт и активности", en => "Sport and activities", kz => "Спорт және жаттығулар"');
insert into partypal_event.category (name, code, languages) VALUES ('"Культура и искусство"', 'Culture' ,'ru => "Культура и искусство", en => "Culture and Art", kz => Мәдениет');
insert into partypal_event.category (name, code, languages) VALUES ('"Кулинария и рестораны"', 'Restaurant' ,'ru => "Кафе и рестораны", en => Restaurants, kz => "Мейрамханалар мен дәмханалар"');
insert into partypal_event.category (name, code, languages) VALUES ('"Фильмы и кино"', 'Education' ,'ru => "Образование и лекции", en => "Education and lectures", kz => "Білім және дәрістер"');
insert into partypal_event.category (name, code, languages) VALUES ('"Вечеринки, встречи, сетевые мероприятия и другие события"', 'Party' ,'ru => "Вечеринки, встречи, сетевые мероприятия и другие события", en => "Parties, meetings, networking events and other events", kz => "Кештер, кездесулер, желілік іс-шаралар және басқа да іс-шаралар"');

create table partypal_location.country
(
    id   bigserial
        primary key,
    name varchar(255),
    code varchar(255)
        unique
);
insert into partypal_location.country (name, code) VALUES ('Kazakhstan', 'KZ');


create table partypal_location.time_zone
(
    id         bigserial
        primary key,
    code       varchar(255),
    name       varchar(255),
    utc_offset varchar(255)
);
insert into partypal_location.time_zone(id, code, name, utc_offset) VALUES (34, 'ALMT' ,'"Alma-Ata Time"' ,'"UTC +6"');
insert into partypal_location.time_zone(id, code, name, utc_offset) VALUES (39, 'AQTT' ,'"Aqtobe Time"' ,'"UTC +5"');


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

34 "ru => Абай, en => Abai, kz => Абай" ABA
34 "ru => Акколь, en => Akkol, kz => Ақкөл" AKK
34 "ru => Аксай, en => Aksai, kz => Ақсай" AKS
34 "ru => Аксу, en => Aksu, kz => Ақсу" AKU
39 "ru => Актау, en => Aktau, kz => Ақтау" AKT
39 "ru => Актобе, en => Aktobe, kz => Ақтөбе" AKX
34 "ru => Алга, en => Alga, kz => Алға" ALG
34 "ru => Алматы, en => Almaty, kz => Алматы" ALA
34 "ru => Алтай, en => Altai, kz => Алтай" ALT
39 "ru => Аральск, en => Aralsk, kz => Арал" ARX
34 "ru => Аркалык, en => Arkalyk, kz => Арқалық" ARK
34 "ru => Арыс, en => Arys, kz => Арыс" ARS
34 "ru => Астана, en => Astana, kz => Астана" AST
34 "ru => Атбасар, en => Atbasar, kz => Атбасар" ATB
39 "ru => Атырау, en => Atyrau, kz => Атырау" ATX
34 "ru => Аягоз, en => Ayagoz, kz => Аягөз" AYZ
34 "ru => Байконур, en => Baikonur, kz => Байқоңыр" BKN
34 "ru => Балхаш, en => Balkhash, kz => Балқаш" BXH
34 "ru => Булаево, en => Bulaevo, kz => Булаев" BUL
34 "ru => Державинск, en => Derzhavinsk, kz => Державинск" DRZ
34 "ru => Ерейментау, en => Ereymentau, kz => Ерейментау" ERM
34 "ru => Есик, en => Esik, kz => Есік" ESK
34 "ru => Есиль, en => Esil, kz => Есіл" ESI
39 "ru => Жанаозен, en => Zhanaozen, kz => Жаңаөзен" ZNZ
34 "ru => Жанатас, en => Zhanatas, kz => Жаңатас" ZNT
34 "ru => Жаркент, en => Zharkent, kz => Жаркент" ZHK
34 "ru => Жезказган, en => Zhezkazgan, kz => Жезқазған" DZN
34 "ru => Жем, en => Zhem, kz => Жем" ZHE
34 "ru => Жетысай, en => Zhetysai, kz => Жетісай" ZTS
34 "ru => Житикара, en => Zhitikara, kz => Жітіқара" ZHI
34 "ru => Зайсан, en => Zaysan, kz => Зайсаң" ZSN
34 "ru => Казалинск, en => Kazalinsk, kz => Қазалы" KAZ
34 "ru => Кандыагаш, en => Kandyagash, kz => Қандыағаш" KDY
34 "ru => Караганда, en => Karaganda, kz => Қарағанды" KGF
34 "ru => Каратау, en => Karatau, kz => Қаратау" KRT
34 "ru => Каркаралинск, en => Karkaralinsk, kz => Қарқаралы" KRL
34 "ru => Каскелен, en => Kaskelen, kz => Қаскелең" KSN
34 "ru => Кентау, en => Kentau, kz => Кентау" KEN
34 "ru => Кокшетау, en => Kokshetau, kz => Көкшетау" KOK
34 "ru => Конаев, en => Konaev, kz => Қонаев" KON
34 "ru => Костанай, en => Kostanay, kz => Қостанай" KSY
34 "ru => Косшы, en => Kosshy, kz => Қосшы" KST
34 "ru => Кульсары, en => Kulsary, kz => Құлсары" KUL
34 "ru => Курчатов, en => Kurchatov, kz => Курчатов" KUR
34 "ru => Кызылорда, en => Kyzylorda, kz => Қызылорда" KZO
34 "ru => Ленгер, en => Lenger, kz => Леңгір" LEN
34 "ru => Лисаковск, en => Lisakovsk, kz => Лисаковск" LSK
34 "ru => Макинск, en => Makinsk, kz => Макинск" MKI
34 "ru => Мамлютка, en => Mamlyutka, kz => Мамлют" MML
34 "ru => Павлодар, en => Pavlodar, kz => Павлодар" PVD
34 "ru => Петропавловск, en => Petropavlovsk, kz => Петропавловск" PTR
34 "ru => Приозёрск, en => Priozersk, kz => Приозерск" PRZ
34 "ru => Риддер, en => Ridder, kz => Риддер" RDR
34 "ru => Рудный, en => Rudny, kz => Рудный" RDN
34 "ru => Сарань, en => Saran, kz => Сарань" SRN
34 "ru => Сарканд, en => Sarkand, kz => Сарқанд" SRK
34 "ru => Сарыагаш, en => Saryagash, kz => Сарыағаш" SYG
34 "ru => Сатпаев, en => Satpaev, kz => Сәтбаев" STP
34 "ru => Семей, en => Semey, kz => Семей" SEM
34 "ru => Сергеевка, en => Sergeevka, kz => Сергеевка" SRV
34 "ru => Серебрянск, en => Serebryansk, kz => Серебрянск" SRB
34 "ru => Степногорск, en => Stepnogorsk, kz => Степногорск" STG
34 "ru => Степняк, en => Stepnyak, kz => Степняк" STN
34 "ru => Тайынша, en => Tayinsha, kz => Тайынша" TYS
34 "ru => Талгар, en => Talgar, kz => Талғар" TLR
34 "ru => Талдыкорган, en => Taldykorgan, kz => Талдықорған" TDK
34 "ru => Тараз, en => Taraz, kz => Тараз" TRZ
34 "ru => Текели, en => Tekeli, kz => Текелі" TKL
34 "ru => Темир, en => Temir, kz => Темір" TMR
34 "ru => Темиртау, en => Temirtau, kz => Теміртау" TMT
34 "ru => Тобыл, en => Tobyl, kz => Тобыл" TBL
34 "ru => Туркестан, en => Turkestan, kz => Түркістан" TRK
39 "ru => Уральск, en => Uralsk, kz => Орал" URL
34 "ru => Усть-Каменогорск, en => Ust-Kamenogorsk, kz => Өскемен" UST
34 "ru => Ушарал, en => Usharal, kz => Үшарал" USR
34 "ru => Уштобе, en => Ushtobe, kz => Үштөбе" USH
34 "ru => Форт-Шевченко, en => ""Fort Shevchenko"", kz => Форт-Шевченко" FSH
34 "ru => Хромтау, en => Khromtau, kz => Хромтау" HRT
34 "ru => Шалкар, en => Shalkar, kz => Шалқар" SHL
34 "ru => Шар, en => Shar, kz => Шар" SHA
34 "ru => Шардара, en => Shardara, kz => Шардара" SHD
34 "ru => Шахтинск, en => Shakhtinsk, kz => Шахтинск" SHK
34 "ru => Шемонаиха, en => Shemonaiha, kz => Шемонаиха" SMH
34 "ru => Шу, en => Shu, kz => Шу" SHU
34 "ru => Шымкент, en => Shymkent, kz => Шымкент" SHM
34 "ru => Щучинск, en => Shchuchinsk, kz => Щучинск" SCH
34 "ru => Эмба, en => Emba, kz => Ембі" EMB
34 "ru => Экибастуз, en => Ekibastuz, kz => Екібастұз" EKB

create table partypal_tg.state
(
    id   bigserial
        primary key,
    code varchar not null
        unique
);
EVENT_CREATED
EVENT_CREATED_NAME_SELECTED
EVENT_CREATED_DESCRIPTION_SELECTED
EVENT_CREATED_REQUIREMENTS_SELECTED
EVENT_CREATED_CATEGORY_SELECTED
EVENT_CREATED_CITY_SELECTED
EVENT_CREATED_LOCATION_SELECTED
EVENT_CREATED_DATE_SELECTED
EVENT_CREATED_TIME_SELECTED
EVENT_UPDATE
EVENT_UPDATE_CITY_SELECT
EVENT_UPDATE_CATEGORY_SELECT
EVENT_UPDATE_LOCATION_SELECT
EVENT_UPDATE_NAME_SELECT
EVENT_UPDATE_DESCRIPTION_SELECT
EVENT_UPDATE_REQUIREMENTS_SELECT
EVENT_UPDATE_DATE_SELECT
EVENT_UPDATE_TIME_SELECT
EVENT_DELETE
MINE_EVENT_SELECTED
ENROLLED_EVENT_SELECTED
SOME_EVENT_SELECTED
REMARK_CREATE
ENROLL_CREATE
REMARK_DELETE


create table partypal_user.gender
(
    id       bigserial
        primary key,
    name     varchar(255),
    code     varchar(255)
        unique,
    language hstore
);
insert into partypal_user.gender (name, code, language) values ('Мужской', 'MALE', '"ru => Мужской, en => Male, kz => Ер"');
insert into partypal_user.gender (name, code, language) values ('Женский', 'FEMALE', '"ru => Женский, en => Female, kz => Әйел"');
insert into partypal_user.gender (name, code, language) values ('Неопределенный', 'UNDEFINED', '"ru => Неопределенный, en => Undefined, kz => Белгісіз"');

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
values ('Администратор', 'ADMIN' ,'"ru => Администратор, en => Admin, kz => Администратор"');
insert into partypal_user.role (name, code, language)
values ('Пользователь', 'USER' ,'"ru => Пользователь, en => User, kz => Пользователь"');

create table user_status
(
    id   bigserial
        primary key,
    name varchar(255),
    code varchar(255)
        unique
);
Активный ACTIVE
Неактивный INACTIVE
Заблокированный BLOCKED

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
