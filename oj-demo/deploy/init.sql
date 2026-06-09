create table tb_sys_user (
user_id      bigint unsigned not null  comment '用户id(主键)',
user_account varchar(20) not null  comment '账号',
password     char(60) not null  comment '密码',
nick_name     varchar(20) comment '昵称',
create_by    bigint unsigned not null  comment '创建人',
create_time  datetime not null  comment '创建时间',
update_by    bigint unsigned  comment '更新人',
update_time  datetime  comment '更新时间',
primary key (`user_id`),
unique key `idx_user_account` (`user_account`)
);





 create table tb_question(
     question_id bigint unsigned not null comment '题目id',
     title varchar(50) not null comment '题目标题',
     difficulty tinyint not null comment '题目难度1:简单 2.中等 3.困难',
     time_limit int not null comment '时间限制',
     space_limit int not null comment '空间限制',
     content varchar(1000) comment '题目内容',
     question_case varchar(1000) comment '题目用例',
     default_code varchar(500) not null comment '默认代码块',
     main_fuc varchar(500) not null comment 'main 函数',
     create_by    bigint unsigned not null  comment '创建人',
     create_time  datetime not null  comment '创建时间',
     update_by    bigint unsigned  comment '更新人',
     update_time  datetime  comment '更新时间',
     primary key (`question_id`)
 );






create table tb_exam(
    exam_id bigint unsigned not null comment '竞赛id(主键)',
    title varchar(50) not null comment '竞赛标题',
    start_time datetime  not null comment '竞赛开始时间',
    end_time datetime  not null comment '竞赛结束时间',
    status tinyint not null default '0' comment '是否发布',
    create_by    bigint unsigned not null  comment '创建人',
    create_time  datetime not null  comment '创建时间',
    update_by    bigint unsigned  comment '更新人',
    update_time  datetime  comment '更新时间',
    primary key(exam_id)
)


create table tb_exam_question(
    exam_question_id bigint unsigned not null comment '竞赛题目关系id(主键)',
    question_id bigint unsigned not null comment '题目id(主键)',
    exam_id bigint unsigned not null comment '竞赛id(主键)',
    question_order int not null comment '题目顺序',
    create_by    bigint unsigned not null  comment '创建人',
    create_time  datetime not null  comment '创建时间',
    update_by    bigint unsigned  comment '更新人',
    update_time  datetime  comment '更新时间',
    primary key(exam_question_id)
)



-- c 端用户管理
create table tb_user(
    user_id bigint unsigned not null comment '用户id(主键)',
    nick_name varchar(20) comment '用户昵称',
    head_image varchar(100) comment '用户头像',
    sex tinyint comment '用户性别',
    phone char(11) not null comment '手机号',
    code char(6)  comment '验证码',
    email varchar(20) comment '昵称',
    wechat varchar(20) comment '微信号',
    school_name varchar(20) comment '学校',
    major_name varchar(20) comment '专业',
    introduce varchar(100) comment '个人介绍',
    status tinyint not null comment '用户状态',
    create_by    bigint unsigned not null  comment '创建人',
    create_time  datetime not null comment '创建时间',
    update_by    bigint unsigned  comment '更新人',
    update_time  datetime comment '更新时间',
    primary key(`user_id`)
)


竞赛报名
create table tb_user_exam(
    user_exam_id bigint unsigned not null comment '用户竞赛关系id(主键)',
    user_id bigint unsigned not null comment '用户id(主键)',
    exam_id bigint unsigned not null comment '竞赛id(主键)',
    score int unsigned comment '得分',
    exam_rank int unsigned comment '排名',
    create_by    bigint unsigned not null  comment '创建人',
    create_time  datetime not null comment '创建时间',
    update_by    bigint unsigned  comment '更新人',
    update_time  datetime comment '更新时间',
    primary key(`user_exam_id`)
)


用户提交表
create table tb_user_submit(
    submit_id bigint unsigned not null comment '提交记录id(主键)',
    user_id  bigint unsigned not null comment '用户id(主键)',
    exam_id  bigint unsigned comment '竞赛id(主键)',
    question_id  bigint unsigned not null comment '题目id(主键)',
    program_type tinyint not null comment '代码类型 0: java 1: c++',
    user_code text not null comment '用户代码',
    `pass` tinyint not null comment '0: 未通过,1: 通过',
    exe_message varchar(500) not null comment '执行结果',
    score int not null default '0' comment '得分',
    create_by    bigint unsigned not null  comment '创建人',
    create_time  datetime not null comment '创建时间',
    update_by    bigint unsigned  comment '更新人',
    update_time  datetime comment '更新时间',

    primary key(`submit_id`)

)


我的消息功能

站内信: 网站内部的一种通信方式
    1.用户和用户之间通信
    2.管理原/系统 和 某个用户之间的通信  ----> 竞赛结果的通信消息
    3.管理员/系统 和 某个用户群之间的通信

主键id 消息标题 消息内容 接收人 发送人

----------------------------------------

消息内容表
create table tb_message_text(
    text_id bigint unsigned not null comment '消息内容id(主键)',
    message_title varchar(10) not null comment '消息标题',
    message_content varchar(200) not null comment '消息内容',
    create_by    bigint unsigned not null  comment '创建人',
    create_time  datetime not null comment '创建时间',
    update_by    bigint unsigned  comment '更新人',
    update_time  datetime comment '更新时间',

    primary key(text_id)
)



消息表
create table tb_message(
    message_id bigint unsigned not null comment '消息id(主键)',
    text_id bigint unsigned not null comment '消息内容id',
    send_id bigint unsigned not null comment '消息发送人id',
    rec_id bigint unsigned not null comment '消息接收人id',
    create_by    bigint unsigned not null  comment '创建人',
    create_time  datetime not null comment '创建时间',
    update_by    bigint unsigned  comment '更新人',
    update_time  datetime comment '更新时间',
    primary key(message_id)
)



















