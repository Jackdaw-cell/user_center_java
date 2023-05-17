package com.jackdaw.javapro.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 * 继承序列化接口 Serializable
 */
@Data
public class UserRegisterRequest implements Serializable {

    public static final long serialVersionUID=319124176373120793L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;
}
