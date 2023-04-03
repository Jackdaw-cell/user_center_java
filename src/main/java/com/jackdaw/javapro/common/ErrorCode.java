package com.jackdaw.javapro.common;


/**
 * 错误异常类
 * @author Jackdaw
 */

public enum ErrorCode {

    /**
     * 异常类及状态码信息
     */
    SUCCESS(0,"ok",""),

    PARAMS_ERROR(40000,"请求参数错误",""),

    NULL_ERROR(40001,"请求参数为空",""),

    NOT_LOGIN(40100,"未登录",""),

    NO_AUTH(40101,"无权限",""),

    SYSTEM_ERROR(40500,"系统错误","");
    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    /**
     * 描述
     */
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
