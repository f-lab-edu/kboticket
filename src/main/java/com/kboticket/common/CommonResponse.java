package com.kboticket.common;

import com.kboticket.common.constants.ResponseConstant;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommonResponse<T> {
    private Integer code;
    private String message;
    private T body;

    public CommonResponse(T body) {
        this.code = ResponseConstant.SUCCESS;
        this.body = body;
    }

    public CommonResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
