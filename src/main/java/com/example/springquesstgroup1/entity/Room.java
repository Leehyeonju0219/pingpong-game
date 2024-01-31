package com.example.springquesstgroup1.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Getter
@Setter
public class Room {
    @Id
    private int id;
    private String title;
    private int host;
    private String room_type;
    private String status;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
