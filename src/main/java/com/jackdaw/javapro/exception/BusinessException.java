package com.jackdaw.javapro.exception;

import com.jackdaw.javapro.common.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Jackdaw
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException{

    private final int code;

    private final String descripton;

    public BusinessException(int code,String message,String descripton){
        super(message);
        this.code=code;
        this.descripton=descripton;
    }

    public BusinessException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.code=errorCode.getCode();
        this.descripton=errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode,String descripton){
        super(errorCode.getMessage());
        this.code=errorCode.getCode();
        this.descripton=descripton;
    }
}
