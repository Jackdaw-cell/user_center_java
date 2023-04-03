package com.jackdaw.javapro.exception;

import com.jackdaw.javapro.common.BaseResponse;
import com.jackdaw.javapro.common.ErrorCode;
import com.jackdaw.javapro.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Jackdaw
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException e){
        log.error("businessException"+e.getMessage(),e);
        return ResultUtils.error(e.getCode(),e.getMessage(),e.getDescripton());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException e){
        log.error("runtimeException"+e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR.getCode(),e.getMessage(),"");
    }

}
