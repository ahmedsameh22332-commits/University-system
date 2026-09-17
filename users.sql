    USE login_schema;

    drop table if exists prerequisites;
    drop table if exists enrollments;
    drop table if exists courses;
    drop table if exists students;
    drop table if exists professor;
    drop table if exists admin;
    drop table if exists pending_registrations;
    drop table if exists users;

    create table users(
        id int primary key auto_increment,
        role int,
        password varchar(255) not null,
        name varchar(50) not null,
        address varchar(255)
    );

    create table students(
        id int,
        gpa float,
        primary key (id),
        foreign key (id) references users(id)
    );

    create table professor(
        id int,
        degree varchar(255),
        primary key (id),
        foreign key (id) references users(id)
    );

    create table admin(
        id int,
        admin_level int,
        primary key (id),
        foreign key (id) references users(id)
    );

    create table courses(
        course_id int primary key,
        course_name varchar(100),
        professor_id int,
        min_gpa_required float,
        credit_hours int not null default 3,
        foreign key (professor_id) references professor(id)
    );

    create table prerequisites(
        course_id int,
        prerequisite_course_id int,
        primary key (course_id, prerequisite_course_id),
        foreign key (course_id) references courses(course_id),
        foreign key (prerequisite_course_id) references courses(course_id)
    );

    create table enrollments(
        student_id int,
        course_id int,
        term varchar(20),
        grade float,
        status int,
        primary key (student_id, course_id, term),
        foreign key (student_id) references students(id),
        foreign key (course_id) references courses(course_id)
    );
    create table pending_registrations(
        request_id int primary key auto_increment,
        name varchar(50),
        address varchar(255),
        role int,
        password varchar(255),
        status int default 1,
        reason varchar(255),
        degree varchar(255),
        course_id int,
        assigned_id int
    );

    insert into users values (2, 1, '123', 'mohamed', 'maadi');
    insert into users values (3, 1, '123', 'mostafa', '6 october');
    insert into users values (4, 1, '123', 'yousef', 'altahrir');
    insert into users values (5, 1, '123', 'mariam', 'bulaq');

    insert into students values (2, 3.14);
    insert into students values (3, 3.38);
    insert into students values (4, 3.54);
    insert into students values (5, 2.54);

    insert into users values (11, 2, '123', 'DR.attia', 'maadi');
    insert into users values (12, 2, '123', 'DR.salwa', '6 october');
    insert into users values (13, 2, '123', 'DR.helal', 'helwan');
    insert into users values (14, 2, '123', 'DR.ghada', 'bulaq');

    insert into professor values (11, 'masters');
    insert into professor values (12, 'bacheloar');
    insert into professor values (13, 'doctrate');
    insert into professor values (14, 'doctrate');

    insert into users values (21, 3, '123', 'admin1', 'maadi');
    insert into users values (22, 3, '123', 'admin2', '6 october');

    insert into admin values (21, 1);
    insert into admin values (22, 2);

    insert into courses (course_id, course_name, professor_id, min_gpa_required, credit_hours) values (101, 'Algorithms', 13, 0, 3);
    insert into courses (course_id, course_name, professor_id, min_gpa_required, credit_hours) values (102, 'Machine Learning', 11, 2.5, 3);
    insert into courses (course_id, course_name, professor_id, min_gpa_required, credit_hours) values (103, 'Artificial Intelligence', 12, 2.5, 3);
    insert into courses (course_id, course_name, professor_id, min_gpa_required, credit_hours) values (104, 'IT Fundamentals', 14, 0, 2);

    insert into prerequisites values (102, 101);
    insert into prerequisites values (103, 101);

    insert into enrollments values (4, 101, 'Fall2025', 3.7, 2);
    insert into enrollments values (2, 101, 'Fall2025', 3.0, 2);
    insert into enrollments values (2, 102, 'Spring2026', null, 1);
    insert into enrollments values (5, 104, 'Spring2026', null, 1);

    insert into pending_registrations (name, address, role, password, status)
    values ('khaled', 'nasr city', 1, '123', 1);

    UPDATE users SET password = '123' WHERE id IN (2,3,4,5,11,12,13,14,21,22);
    UPDATE pending_registrations SET password = '123' WHERE name = 'khaled';