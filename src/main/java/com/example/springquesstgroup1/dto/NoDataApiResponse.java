package com.example.springquesstgroup1.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NoDataApiResponse {
    private Integer code;
    private String message;
}
