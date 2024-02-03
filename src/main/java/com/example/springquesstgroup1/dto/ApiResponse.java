package com.example.springquesstgroup1.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponse<T> {
    private Integer code;
    private String message;
    private T result;
}
