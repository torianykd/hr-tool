create table departments
(
    id   int unsigned auto_increment,
    name varchar(100) not null,
    constraint departments_pk
        primary key (id)
);

create unique index departments_name_uindex
    on departments (name);

create table positions
(
    id            int unsigned auto_increment,
    department_id int unsigned null,
    name          varchar(255) not null,
    constraint positions_pk
        primary key (id),
    constraint positions_departments_id_fk
        foreign key (department_id) references departments (id)
            on delete set null
);

create unique index positions_name_uindex
    on positions (name);

create table employees
(
    id          int unsigned auto_increment,
    email       varchar(255) not null,
    first_name  varchar(255) not null,
    last_name   varchar(255) not null,
    birth_date  timestamp    not null,
    hiring_date timestamp    not null,
    constraint employees_pk
        primary key (id)
);

create unique index employees_email_uindex
    on employees (email);

create table employee_position
(
    employee_id int unsigned not null,
    position_id int unsigned not null,
    primary key (employee_id, position_id),
    constraint employee_position_employees_fk foreign key (employee_id)
        references employees (id) on delete cascade,
    constraint employee_position_positions_fk foreign key (position_id)
        references positions (id) on delete cascade
);

create table contacts
(
    id          int unsigned auto_increment,
    employee_id int unsigned            not null,
    type        enum ('PHONE', 'SKYPE') not null,
    value       varchar(255)            not null,
    constraint contacts_pk
        primary key (id),
    constraint contacts_employee_id_fk foreign key (employee_id)
        references employees (id) on delete cascade
);

create table leave_requests
(
    id          int unsigned auto_increment,
    employee_id int unsigned                                                            not null,
    type        enum ('PAYABLE', 'NON_PAYABLE', 'SICK_LEAVE', 'REMOTE_WORK', 'SHIFTED') not null,
    status      enum ('PENDING', 'APPROVED', 'DECLINED')                                not null default 'PENDING',
    start       timestamp                                                               not null,
    end         timestamp                                                               not null,
    comment     varchar(255),
    constraint leave_requests_pk
        primary key (id),
    constraint leave_requests_employee_id_fk foreign key (employee_id)
        references employees (id) on delete cascade
);
