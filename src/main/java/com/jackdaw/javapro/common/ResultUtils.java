package com.jackdaw.javapro.common;

/**
 * 结果返回工具类
 * @author Jackdaw
 */
public class ResultUtils {
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0, data,"ok");
    }

    public static BaseResponse error(ErrorCode errorCode){
        return new BaseResponse(errorCode.getCode(),null, errorCode.getMessage(),errorCode.getDescription());
    }

    public static BaseResponse error(int code, String message, String description) {
        return new BaseResponse(code,null, message, description);
    }
}
