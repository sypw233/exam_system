create table online_exam_system.exam_questions
(
    exam_id     bigint not null,
    question_id bigint not null,
    score       int    not null,
    primary key (exam_id, question_id),
    constraint exam_questions_ibfk_1
        foreign key (exam_id) references online_exam_system.exams (id)
            on delete cascade,
    constraint exam_questions_ibfk_2
        foreign key (question_id) references online_exam_system.questions (id)
            on delete cascade
);

create index question_id
    on online_exam_system.exam_questions (question_id);

