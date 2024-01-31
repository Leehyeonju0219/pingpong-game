package com.example.springquesstgroup1.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Getter
@Setter
public class User {
    @Id
    private int id;
    private int fakerId;
    private String name;
    private String email;
    private String status;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
