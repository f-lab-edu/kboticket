package com.kboticket.dto;

import lombok.Data;

@Data
public class TermsDto {
    private String title;
    private String version;
    private String content;
    private boolean mandatory;

}
