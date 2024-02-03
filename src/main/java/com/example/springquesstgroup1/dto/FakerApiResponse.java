package com.example.springquesstgroup1.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FakerApiResponse<T> {
    private String status;
    private int code;
    private int total;
    private T data;

}
