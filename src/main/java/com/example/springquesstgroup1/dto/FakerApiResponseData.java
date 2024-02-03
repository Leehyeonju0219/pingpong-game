package com.example.springquesstgroup1.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FakerApiResponseData {
    private int id;
    private String uuid;
    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private String email;
    private String ip;
    private String macAddress;
    private String website;
    private String image;
}
