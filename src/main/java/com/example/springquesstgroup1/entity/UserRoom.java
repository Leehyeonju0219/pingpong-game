package com.example.springquesstgroup1.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class UserRoom {
    @Id
    private int id;
    private int room_id;
    private int user_id;
    private String team;
}
