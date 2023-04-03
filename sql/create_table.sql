-- auto-generated definition
create table user
(
    id           bigint                             not null comment '主键ID'
        primary key,
    username     varchar(256)                       null comment '姓名',
    email        varchar(512)                       null comment '邮箱',
    userAccount  varchar(256)                       null comment '账号',
    userPassword varchar(512)                       null comment '密码',
    avatarUrl    varchar(1024)                      null comment '头像',
    gender       tinyint                            null comment '性别',
    phone        varchar(128)                       null comment '电话',
    userStatus   int                                null comment '用户状态',
    createTime   datetime default CURRENT_TIMESTAMP null,
    updateTime   datetime default CURRENT_TIMESTAMP null,
    isDelete     tinyint  default 0                 null,
    userRole     int      default 0                 null comment '角色 0-普通用户 1-管理员 2-会员',
    planetCode   varchar(512)                       null comment '星球编号'
);

