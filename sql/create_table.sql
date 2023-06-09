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

create table if not exists tag
(
    id bigint not null comment '主键ID'
    primary key,
    tagName varchar(256) null comment '标签名称',
    userId bigint null comment '用户id',
    parentId bigint null comment '父标签id',
    isParent tinyint null comment '0 - 不是父标签 , 1- 是父标签',
    createTime datetime default CURRENT_TIMESTAMP null,
    updateTime datetime default CURRENT_TIMESTAMP null,
    isDelete tinyint default 0 null,
    constraint tag_tagName_uindex
    unique (tagName),
    constraint tag_userId_uindex
    unique (userId)
    )
    comment '标签';

create table team
(   id              bigint auto_increment comment 'id' primary key,
    name            varchar(256) not null comment '队伍名称',
    description     varchar(1024)null comment '描述',
    maxNum          int default 1 not null comment '最大人数',
    expireTime      datetime null comment'过期时间',
    userId          bigint comment '用户id',
    status          int default 0 not null comment '0-公开，1-私有，2-加密',
    password        varchar(512) null comment'密码',
    createTime      datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete        tinyint default 0 not null comment '是否删除'
)   comment'队伍';

create table user_team
(       id              bigint auto_increment comment 'id' primary key,
        userId           bigint comment '用户id',
        teamId           bigint comment '队伍id',
        joinTime         datetime null comment '加入时间',
        createTime      datetime default CURRENT_TIMESTAMP null comment '创建时间',
        updateTime      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
        isDelete        tinyint default 0 not null comment '是否删除'
)   comment '用户队伍关系';

create table chat_connect
(
    id         int auto_increment comment 'ID主键'
        primary key,
    fromName   varchar(255) null comment '连接发起者',
    toName     varchar(255) null comment '连接接收者',
    createTime datetime  default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime  default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '创建时间',
    del_flag   tinyint default 0 not null comment '是否删除'
)
    comment '会话连接记录';