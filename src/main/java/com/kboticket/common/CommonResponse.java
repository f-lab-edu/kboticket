package com.kboticket.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommonResponse<T> {

    private Integer code;
    private String message;
    private T body;

    public CommonResponse(Integer code) {
        this.code = code;
    }

}
